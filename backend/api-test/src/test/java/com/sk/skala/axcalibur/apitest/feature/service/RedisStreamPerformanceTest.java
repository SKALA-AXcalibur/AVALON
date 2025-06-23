package com.sk.skala.axcalibur.apitest.feature.service;

import static org.assertj.core.api.Assertions.*;

import com.sk.skala.axcalibur.apitest.feature.code.StreamConstants;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import com.sk.skala.axcalibur.apitest.feature.util.ApiTaskDtoConverter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379",
        "logging.level.com.sk.skala.axcalibur.apitest.feature.service.RedisStreamListener=WARN"
})
@DisplayName("Redis Streams 성능 테스트")
// @EnabledIfSystemProperty(named = "performance.test", matches = "true")
class RedisStreamPerformanceTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        // 테스트 전에 스트림의 PENDING 메시지들을 정리
        try {
            // 컨슈머 그룹이 존재하는지 확인하고 생성
            try {
                redisTemplate.opsForStream().createGroup(StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME);
            } catch (Exception e) {
                // 그룹이 이미 존재하는 경우 무시
            }
            // PENDING 메시지들을 ACK 처리하여 정리
            var pendingMessages = redisTemplate.opsForStream()
                    .pending(StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME);

            if (pendingMessages != null && pendingMessages.getTotalPendingMessages() > 0) {
                var pendingDetails = redisTemplate.opsForStream()
                        .pending(StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME,
                                org.springframework.data.domain.Range.unbounded(),
                                pendingMessages.getTotalPendingMessages());

                for (var pending : pendingDetails) {
                    redisTemplate.opsForStream().acknowledge(StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME,
                            pending.getId());
                }
            }
        } catch (Exception e) {
            // 스트림이 존재하지 않을 수 있으므로 무시
        }
    }

    @Test
    @DisplayName("대량 메시지 처리 성능 테스트 (100개 메시지)")
    void performanceTest_shouldHandleLargeNumberOfMessages() {
        // Given
        int messageCount = 100;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Instant startTime = Instant.now(); // When - 대량 메시지를 비동기로 전송
        for (int i = 0; i < messageCount; i++) {
            final int messageId = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                ApiTaskDto apiTaskDto = createApiTaskDto(
                        "GET",
                        "https://httpbin.org/delay/1",
                        null,
                        null,
                        messageId);
                var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);
                var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
                redisTemplate.opsForStream().add(record);
            });
            futures.add(future);
        }

        // 모든 메시지 전송 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        Instant sendEndTime = Instant.now();
        Duration sendDuration = Duration.between(startTime, sendEndTime);

        System.out.println("Message transmission completion time: " + sendDuration.toMillis() + "ms");

        // Then - 모든 메시지가 처리될 때까지 대기 (최대 5분)
        long processingStartTime = System.currentTimeMillis();
        boolean allProcessed = false;

        while (!allProcessed && (System.currentTimeMillis() - processingStartTime) < 300_000) {
            try {
                var pendingMessages = redisTemplate.opsForStream()
                        .pending(StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME);

                if (pendingMessages != null && pendingMessages.getTotalPendingMessages() == 0) {
                    allProcessed = true;
                } else {
                    Thread.sleep(1000); // 1초 대기
                    if (pendingMessages != null) {
                        System.out.println("message left: " + pendingMessages.getTotalPendingMessages());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error checking pending messages: " + e.getMessage());
                break;
            }
        }

        Instant endTime = Instant.now();
        Duration totalDuration = Duration.between(startTime, endTime);

        // 성능 결과 출력
        System.out.println("=== Performance test results ===");
        System.out.println("total number of messages: " + messageCount);
        System.out.println("Total processing time: " + totalDuration.toMillis() + "ms");
        System.out.println("Average processing time per message: " + (totalDuration.toMillis() / messageCount) + "ms");
        System.out.println(
                "throughput per second: " + (messageCount * 1000.0 / totalDuration.toMillis()) + " messages/sec");

        assertThat(allProcessed).isTrue();
        assertThat(totalDuration.toMinutes()).isLessThan(5); // 5분 이내 완료
    }

    @Test
    @DisplayName("동시 처리 능력 테스트 (빠른 메시지 50개)")
    void performanceTest_shouldHandleConcurrentMessages() {
        // Given
        int messageCount = 50;
        AtomicInteger successCount = new AtomicInteger(0);
        Instant startTime = Instant.now();

        // When - 빠른 API 엔드포인트로 동시 처리 테스트
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < messageCount; i++) {
            final int messageId = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                ApiTaskDto apiTaskDto = createApiTaskDto(
                        "GET",
                        "https://httpbin.org/uuid", // 빠른 응답 엔드포인트
                        null,
                        null,
                        messageId);
                var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);
                var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
                redisTemplate.opsForStream().add(record);
                successCount.incrementAndGet();
            });
            futures.add(future);
        }

        // 모든 전송 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Then - 모든 메시지 처리 대기 (최대 2분)
        long processingStartTime = System.currentTimeMillis();
        boolean allProcessed = false;

        while (!allProcessed && (System.currentTimeMillis() - processingStartTime) < 120_000) {
            try {
                var pendingMessages = redisTemplate.opsForStream()
                        .pending(StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME);

                if (pendingMessages != null && pendingMessages.getTotalPendingMessages() == 0) {
                    allProcessed = true;
                } else {
                    Thread.sleep(500); // 0.5초 대기
                }
            } catch (Exception e) {
                break;
            }
        }

        Instant endTime = Instant.now();
        Duration totalDuration = Duration.between(startTime, endTime);

        // 성능 결과 출력
        System.out.println("=== Concurrent processing test results ===");
        System.out.println("transfer success: " + successCount.get() + "/" + messageCount);
        System.out.println("Total processing time: " + totalDuration.toMillis() + "ms");
        System.out.println(
                "throughput per second: " + (messageCount * 1000.0 / totalDuration.toMillis()) + " messages/sec");

        assertThat(successCount.get()).isEqualTo(messageCount);
        assertThat(allProcessed).isTrue();
        assertThat(totalDuration.toMinutes()).isLessThan(2); // 2분 이내 완료
    }

    @Test
    @DisplayName("메모리 사용량 테스트")
    void performanceTest_shouldNotExceedMemoryLimits() {
        // Given
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        int messageCount = 200;

        System.out.println("Initial memory usage: " + (initialMemory / 1024 / 1024) + " MB");

        // When
        for (int i = 0; i < messageCount; i++) {
            ApiTaskDto apiTaskDto = createApiTaskDto(
                    "GET",
                    "https://httpbin.org/get",
                    null,
                    null,
                    i);
            var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);
            var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
            redisTemplate.opsForStream().add(record);
        }

        // 짧은 대기 후 메모리 사용량 측정
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long currentMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = currentMemory - initialMemory;

        System.out.println("Current memory usage: " + (currentMemory / 1024 / 1024) + " MB");
        System.out.println("memory increase: " + (memoryIncrease / 1024 / 1024) + " MB");

        // Then - 메모리 증가량이 합리적인 범위 내에 있는지 확인
        assertThat(memoryIncrease).isLessThan(100 * 1024 * 1024); // 100MB 미만 증가
    }

    /**
     * 성능 테스트용 ApiTaskDto 생성 헬퍼 메서드
     */
    private ApiTaskDto createApiTaskDto(String method, String uri,
            Object headers, Object body, int messageId) {
        return ApiTaskDto.builder()
                .id(messageId)
                .resultId(messageId + 10000)
                .precondition("Performance test - " + messageId)
                .step(1)
                .method(method)
                .uri(uri)
                .reqHeader(null)
                .reqBody(null)
                .statusCode(2) // 2XX 응답 기대
                .resHeader(null)
                .resBody(null)
                .build();
    }
}
