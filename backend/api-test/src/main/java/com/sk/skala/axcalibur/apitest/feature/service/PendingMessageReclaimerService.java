package com.sk.skala.axcalibur.apitest.feature.service;

import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.sk.skala.axcalibur.apitest.feature.code.StreamConstants;
import com.sk.skala.axcalibur.apitest.feature.util.ApiTaskDtoConverter;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Redis Stream의 pending 메시지를 자동으로 재할당하는 서비스
 * 
 * XAUTOCLAIM 명령을 사용하여 일정 시간 이상 idle 상태인 pending 메시지를
 * 다른 컨슈머에게 자동으로 재할당합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PendingMessageReclaimerService {

    private final RedisTemplate<String, Object> redis;
    private final TimeBasedGenerator uuid;
    private final RedisStreamListener listener; // 기존 리스너 재활용

    /**
     * pending 메시지가 재할당되기 위한 최소 idle 시간 (밀리초)
     * 기본값: 2분 (120,000ms)
     */
    @Value("${spring.data.redis.stream.pending.min-idle-time:120000}")
    private long minIdleTime;

    /**
     * 한 번에 처리할 pending 메시지 배치 크기
     * 기본값: 10개
     */
    @Value("${spring.data.redis.stream.pending.batch-size:10}")
    private int batchSize;

    /**
     * pending 메시지 재할당 활성화 여부
     * 기본값: true
     */
    @Value("${spring.data.redis.stream.pending.reclaim.enabled:true}")
    private boolean reclaimEnabled;

    /**
     * 스케줄러: 1분 30초마다 pending 메시지 재할당 실행 (Virtual Thread에서 비동기 실행)
     * 
     * - Redis 6.2+ XCLAIM 명령을 사용
     * - 커서 기반으로 모든 pending 메시지를 순차적으로 스캔
     * - 지정된 시간 이상 idle인 메시지만 클레임
     * - 원자적 연산으로 동시성 문제 해결
     * - JDK 21 Virtual Thread에서 비동기 실행으로 메인 스레드 풀 블로킹 방지
     */
    @Scheduled(fixedDelay = 90000) // 1분 30초마다 실행
    @Async("virtualThreadTaskExecutor") // Virtual Thread에서 비동기 실행
    public void reclaimPendingMessages() {
        if (!reclaimEnabled) {
            // 설정에 따라 재할당 비활성화 시 바로 종료
            return;
        }
        try {
            // 재할당 컨슈머 이름(고유값)
            String consumerName = "avalon-reclaimer-" + uuid.generate().toString();
            int totalReclaimed = 0; // 총 재할당된 메시지 수

            log.debug(
                    "PendingMessageReclaimerService.reclaimPendingMessages: Starting pending message reclaim process for consumer: {}",
                    consumerName);
            // 1. 전체 pending 메시지 개수 조회
            var pendingInfo = redis.opsForStream().pending(
                    StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME);

            if (pendingInfo == null || pendingInfo.getTotalPendingMessages() == 0) {
                log.debug("PendingMessageReclaimerService.reclaimPendingMessages: No pending messages found");
                return;
            }

            // 2. pending 메시지 상세 정보(consumer, idle time 등) 조회
            var pendingMessages = redis.opsForStream().pending(
                    StreamConstants.STREAM_KEY,
                    StreamConstants.GROUP_NAME,
                    Range.unbounded(),
                    Math.min(batchSize, pendingInfo.getTotalPendingMessages()));

            if (pendingMessages != null && !pendingMessages.isEmpty()) {
                for (var pendingMessage : pendingMessages) {
                    // 3. idle time 체크 - 지정된 시간 이상 idle인 경우만 재할당 시도
                    if (pendingMessage.getElapsedTimeSinceLastDelivery().toMillis() >= minIdleTime) {
                        try {
                            // 4. XCLAIM으로 메시지 소유권 이전 (Redis 명령)
                            var claimedMessages = redis.opsForStream().claim(
                                    StreamConstants.STREAM_KEY,
                                    StreamConstants.GROUP_NAME,
                                    consumerName,
                                    Duration.ofMillis(minIdleTime),
                                    pendingMessage.getId());
                            if (claimedMessages != null && !claimedMessages.isEmpty()) {
                                totalReclaimed++;
                                log.info(
                                        "PendingMessageReclaimerService.reclaimPendingMessages: Successfully reclaimed message: {} (idle: {}ms)",
                                        pendingMessage.getId(),
                                        pendingMessage.getElapsedTimeSinceLastDelivery().toMillis());
                                // 5. 클레임된 메시지를 기존 리스너로 재처리
                                for (var claimedMessage : claimedMessages) {
                                    try {
                                        // MapRecord<String, Object, Object> → MapRecord<String, String, String> 변환
                                        var stringMap = new java.util.HashMap<String, String>();
                                        claimedMessage.getValue()
                                                .forEach((k, v) -> stringMap.put(k.toString(), v.toString()));

                                        // 리스너가 기대하는 타입으로 변환
                                        var convertedRecord = StreamRecords
                                                .mapBacked(stringMap)
                                                .withStreamKey(StreamConstants.STREAM_KEY)
                                                .withId(claimedMessage.getId());

                                        log.debug(
                                                "PendingMessageReclaimerService.reclaimPendingMessages: Reprocessing reclaimed message: {}",
                                                claimedMessage.getId());

                                        // 기존 리스너의 onMessage 메서드 호출 (실제 메시지 재처리)
                                        listener.onMessage(convertedRecord);

                                        log.debug(
                                                "PendingMessageReclaimerService.reclaimPendingMessages: Successfully reprocessed reclaimed message: {}",
                                                claimedMessage.getId());
                                    } catch (Exception reprocessException) {
                                        log.error(
                                                "PendingMessageReclaimerService.reclaimPendingMessages: Failed to reprocess reclaimed message: {}",
                                                claimedMessage.getId(),
                                                reprocessException);
                                        totalReclaimed--;
                                        // 재처리 실패 시 메시지를 ACK 처리하여 무한 루프 방지
                                        redis.opsForStream().acknowledge(
                                                StreamConstants.GROUP_NAME, claimedMessage);
                                        var stringMap = new java.util.HashMap<String, String>();
                                        claimedMessage.getValue()
                                                .forEach((k, v) -> stringMap.put(k.toString(), v.toString()));
                                        var dto = ApiTaskDtoConverter.fromMap(stringMap);
                                        // 재시도 5회 이하인 경우 재시도 큐에 추가
                                        if (dto.attempt() <= 5) {
                                            log.info(
                                                    "PendingMessageReclaimerService.reclaimPendingMessages: Re-adding message: {} for retry: {}",
                                                    claimedMessage.getId(), dto.attempt());
                                            var newDto = dto.toBuilder()
                                                    .attempt(dto.attempt() + 1)
                                                    .build();
                                            redis.opsForStream().add(
                                                    StreamConstants.STREAM_KEY,
                                                    ApiTaskDtoConverter.toMap(newDto));
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // XCLAIM 자체 실패 시
                            log.warn(
                                    "PendingMessageReclaimerService.reclaimPendingMessages: Failed to claim message: {}",
                                    pendingMessage.getId(), e);
                        }
                    } else {
                        // 아직 idle 시간이 부족한 메시지는 건너뜀
                        log.debug(
                                "PendingMessageReclaimerService.reclaimPendingMessages: Message {} not yet eligible for reclaim (idle: {}ms < {}ms)",
                                pendingMessage.getId(),
                                pendingMessage.getElapsedTimeSinceLastDelivery().toMillis(),
                                minIdleTime);
                    }
                }
            }

            if (totalReclaimed > 0) {
                log.info(
                        "PendingMessageReclaimerService.reclaimPendingMessages: Completed pending message reclaim process. Total reclaimed: {}",
                        totalReclaimed);
            } else {
                log.debug(
                        "PendingMessageReclaimerService.reclaimPendingMessages: No pending messages found for reclaim");
            }

        } catch (Exception e) {
            // 전체 재할당 프로세스 예외 처리
            log.error("PendingMessageReclaimerService.reclaimPendingMessages: Failed to reclaim pending messages", e);
        }
    }

    /**
     * pending 메시지 통계 조회 (모니터링용)
     * 
     * @return pending 메시지 수
     */
    public long getPendingMessageCount() {
        try {
            var pendingInfo = redis.opsForStream().pending(
                    StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME);
            return pendingInfo != null ? pendingInfo.getTotalPendingMessages() : 0;
        } catch (Exception e) {
            log.warn("PendingMessageReclaimerService.getPendingMessageCount: Failed to get pending message count", e);
            return -1;
        }
    }

    /**
     * 수동으로 pending 메시지 재할당 실행 (관리자용)
     */
    public void manualReclaimPendingMessages() {
        log.info(
                "PendingMessageReclaimerService.manualReclaimPendingMessages: Manual pending message reclaim triggered");
        reclaimPendingMessages();
    }
}
