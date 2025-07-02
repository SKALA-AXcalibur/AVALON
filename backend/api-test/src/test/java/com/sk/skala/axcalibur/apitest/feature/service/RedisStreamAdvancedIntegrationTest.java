package com.sk.skala.axcalibur.apitest.feature.service;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

import com.sk.skala.axcalibur.apitest.feature.code.StreamConstants;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import com.sk.skala.axcalibur.apitest.feature.util.ApiTaskDtoConverter;

import java.time.Duration;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379",
        "logging.level.com.sk.skala.axcalibur.apitest.feature.service.RedisStreamListener=DEBUG"
})
@DisplayName("Redis Streams 고급 통합 테스트")
class RedisStreamAdvancedIntegrationTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        // 테스트 전에 스트림의 PENDING 메시지들을 정리
        try {
            // 컨슈머 그룹이 존재하는지 확인하고 생성
            try {
                redisTemplate.opsForStream().createGroup(StreamConstants.STREAM_KEY,
                        StreamConstants.GROUP_NAME);
            } catch (Exception e) {
                // 그룹이 이미 존재하는 경우 무시
            }

            // PENDING 메시지들을 ACK 처리하여 정리
            var pendingMessages = redisTemplate.opsForStream().pending(
                    StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME);

            if (pendingMessages != null && pendingMessages.getTotalPendingMessages() > 0) {
                var pendingDetails = redisTemplate.opsForStream().pending(
                        StreamConstants.STREAM_KEY,
                        StreamConstants.GROUP_NAME,
                        org.springframework.data.domain.Range.unbounded(),
                        pendingMessages.getTotalPendingMessages());

                for (var pending : pendingDetails) {
                    redisTemplate.opsForStream().acknowledge(
                            StreamConstants.STREAM_KEY,
                            StreamConstants.GROUP_NAME,
                            pending.getId());
                }
            }
        } catch (Exception e) {
            // 스트림이 존재하지 않을 수 있으므로 무시
        }
    }

    @Test
    @DisplayName("대량 메시지 처리 성능 테스트")
    void redisStreamIntegration_shouldHandleBulkMessagesEfficiently() {
        // Given - 대량 메시지 생성 (실제 운영에서 발생할 수 있는 시나리오)
        int messageCount = 50;
        List<ApiTaskDto> messages = new ArrayList<>();

        for (int i = 0; i < messageCount; i++) {
            Map<String, Object> body = Map.of(
                    "messageId", "bulk-test-" + i,
                    "timestamp", System.currentTimeMillis(),
                    "payload", "data-" + i);
            messages.add(createApiTaskDto("POST", "https://httpbin.org/post", body));
        }

        // When - 대량 메시지를 빠르게 스트림에 추가
        long startTime = System.currentTimeMillis();
        List<String> recordIds = new ArrayList<>();

        for (ApiTaskDto message : messages) {
            var dataMap = ApiTaskDtoConverter.toMap(message);
            var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
            var recordId = redisTemplate.opsForStream().add(record);
            if (recordId != null) {
                recordIds.add(recordId.getValue());
            }
        }

        long additionTime = System.currentTimeMillis() - startTime;
        System.out.println("Test: Added " + messageCount + " messages in " + additionTime + "ms");
        System.out.println("Test: Average time per message: " + (additionTime / (double) messageCount) + "ms");

        // Then - 모든 메시지가 합리적인 시간 내에 처리되는지 확인
        await().atMost(Duration.ofSeconds(60)).pollInterval(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    var pendingMessages = redisTemplate.opsForStream().pending(
                            StreamConstants.STREAM_KEY,
                            StreamConstants.GROUP_NAME);

                    long remainingMessages = pendingMessages != null ? pendingMessages.getTotalPendingMessages() : 0;

                    System.out.println("Test: Remaining pending messages: " + remainingMessages +
                            "/" + messageCount);

                    assertThat(remainingMessages)
                            .as("모든 대량 메시지가 처리되어야 함")
                            .isZero();
                });

        assertThat(recordIds).hasSize(messageCount);
        assertThat(additionTime).as("메시지 추가 시간이 합리적이어야 함").isLessThan(5000); // 5초 이내
    }

    @Test
    @DisplayName("동시성 테스트 - 여러 스레드에서 동시에 메시지 전송")
    void redisStreamIntegration_shouldHandleConcurrentMessageSending() throws InterruptedException {
        // Given - 멀티스레드 환경 설정
        int threadCount = 5;
        int messagesPerThread = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();

        // When - 여러 스레드에서 동시에 메시지 전송
        for (int threadId = 0; threadId < threadCount; threadId++) {
            final int finalThreadId = threadId;
            CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
                List<String> threadRecordIds = new ArrayList<>();

                for (int msgId = 0; msgId < messagesPerThread; msgId++) {
                    Map<String, Object> body = Map.of(
                            "threadId", finalThreadId,
                            "messageId", msgId,
                            "timestamp", System.currentTimeMillis());

                    ApiTaskDto apiTaskDto = createApiTaskDto("POST", "https://httpbin.org/post", body);
                    var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);
                    var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
                    var recordId = redisTemplate.opsForStream().add(record);

                    if (recordId != null) {
                        threadRecordIds.add(recordId.getValue());
                    }
                }

                System.out.println("Thread " + finalThreadId + " sent " + threadRecordIds.size() + " messages");
                return threadRecordIds;
            }, executor);

            futures.add(future);
        }

        // 모든 스레드 완료 대기
        List<String> allRecordIds = new ArrayList<>();
        for (CompletableFuture<List<String>> future : futures) {
            try {
                allRecordIds.addAll(future.get(30, TimeUnit.SECONDS));
            } catch (ExecutionException | TimeoutException | InterruptedException e) {
                fail("Failed to get future result: " + e.getMessage());
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Executor termination was interrupted: " + e.getMessage());
        }

        System.out.println("Test: Total messages sent across all threads: " + allRecordIds.size());

        // Then - 모든 메시지가 처리되는지 확인
        int expectedTotalMessages = threadCount * messagesPerThread;

        await().atMost(Duration.ofSeconds(90)).pollInterval(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    var pendingMessages = redisTemplate.opsForStream().pending(
                            StreamConstants.STREAM_KEY,
                            StreamConstants.GROUP_NAME);

                    long remainingMessages = pendingMessages != null ? pendingMessages.getTotalPendingMessages() : 0;

                    System.out.println("Test: Concurrent test - Remaining pending messages: " +
                            remainingMessages + "/" + expectedTotalMessages);

                    assertThat(remainingMessages)
                            .as("모든 동시 메시지가 처리되어야 함")
                            .isZero();
                });

        assertThat(allRecordIds).hasSize(expectedTotalMessages);
    }

    @Test
    @DisplayName("Stream 메타데이터 및 모니터링 정보 검증")
    void redisStreamIntegration_shouldProvideAccurateStreamMetadata() {
        // Given - 테스트 메시지들 추가
        int messageCount = 5;
        List<String> recordIds = new ArrayList<>();

        for (int i = 0; i < messageCount; i++) {
            ApiTaskDto apiTaskDto = createApiTaskDto("GET", "https://httpbin.org/get", null);
            var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);
            var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
            var recordId = redisTemplate.opsForStream().add(record);
            if (recordId != null) {
                recordIds.add(recordId.getValue());
            }
        }

        // When & Then - Stream 메타데이터 확인
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            // Stream 기본 정보
            var streamInfo = redisTemplate.opsForStream().info(StreamConstants.STREAM_KEY);
            System.out.println("=== Stream Information ===");
            System.out.println("Stream length: " + streamInfo.streamLength());
            System.out.println("Consumer groups: " + streamInfo.groupCount());
            System.out.println("First entry ID: " + streamInfo.firstEntryId());
            System.out.println("Last entry ID: " + streamInfo.lastEntryId());

            assertThat(streamInfo.streamLength()).isGreaterThanOrEqualTo(messageCount);
            assertThat(streamInfo.groupCount()).isEqualTo(1);
            assertThat(streamInfo.firstEntryId()).isNotNull();
            assertThat(streamInfo.lastEntryId()).isNotNull();

            // Consumer Group 정보
            var groups = redisTemplate.opsForStream().groups(StreamConstants.STREAM_KEY);
            assertThat(groups).hasSize(1);

            var group = groups.get(0);
            System.out.println("\n=== Consumer Group Information ===");
            System.out.println("Group name: " + group.groupName());
            System.out.println("Consumer count: " + group.consumerCount());
            System.out.println("Pending count: " + group.pendingCount());
            System.out.println("Last delivered ID: " + group.lastDeliveredId());

            assertThat(group.groupName()).isEqualTo(StreamConstants.GROUP_NAME);
            assertThat(group.lastDeliveredId()).isNotNull();

            // Consumer 정보 (활성 consumer가 있는 경우)
            var consumers = redisTemplate.opsForStream().consumers(
                    StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME);

            System.out.println("\n=== Consumer Information ===");
            System.out.println("Active consumers count: " + consumers.size());

            for (var consumer : consumers) {
                System.out.println("Consumer: " + consumer.consumerName() +
                        ", Pending: " + consumer.pendingCount() +
                        ", Idle time: " + consumer.idleTime() + "ms");

                assertThat(consumer.consumerName()).isNotBlank();
                assertThat(consumer.idleTime()).isGreaterThanOrEqualTo(Duration.ZERO);
            }
        });

        assertThat(recordIds).hasSize(messageCount);
    }

    @Test
    @DisplayName("다양한 HTTP 메서드 조합 테스트")
    void redisStreamIntegration_shouldHandleVariousHttpMethods() {
        // Given - 다양한 HTTP 메서드들
        List<ApiTaskDto> messages = List.of(
                createApiTaskDto("GET", "https://httpbin.org/get", null),
                createApiTaskDto("POST", "https://httpbin.org/post", Map.of("data", "test")),
                createApiTaskDto("PUT", "https://httpbin.org/put", Map.of("update", "data")),
                createApiTaskDto("DELETE", "https://httpbin.org/delete", null),
                createApiTaskDto("PATCH", "https://httpbin.org/patch", Map.of("patch", "data")));

        // When - 모든 메시지를 스트림에 추가
        List<String> recordIds = new ArrayList<>();
        for (ApiTaskDto message : messages) {
            var dataMap = ApiTaskDtoConverter.toMap(message);
            var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
            var recordId = redisTemplate.opsForStream().add(record);
            if (recordId != null) {
                recordIds.add(recordId.getValue());
                System.out.println("Test: Sent " + message.method() + " request with ID: " + recordId);
            }
        }

        // Then - 모든 HTTP 메서드가 처리되는지 확인
        await().atMost(Duration.ofSeconds(30)).pollInterval(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                    var pendingMessages = redisTemplate.opsForStream().pending(
                            StreamConstants.STREAM_KEY,
                            StreamConstants.GROUP_NAME);

                    long remainingMessages = pendingMessages != null ? pendingMessages.getTotalPendingMessages() : 0;

                    System.out.println("Test: HTTP methods test - Remaining pending messages: " +
                            remainingMessages + "/" + messages.size());

                    assertThat(remainingMessages)
                            .as("모든 HTTP 메서드 요청이 처리되어야 함")
                            .isZero();
                });

        assertThat(recordIds).hasSize(messages.size());
    }

    @Test
    @DisplayName("오류 시나리오 및 복구 테스트")
    void redisStreamIntegration_shouldHandleErrorScenariosGracefully() {
        // Given - 정상 메시지와 오류 메시지 혼합
        List<ApiTaskDto> messages = List.of(
                createApiTaskDto("GET", "https://httpbin.org/get", null), // 정상
                createApiTaskDto("GET", "http://invalid-domain-123456.com/test", null), // 오류
                createApiTaskDto("POST", "https://httpbin.org/post", Map.of("data", "test")), // 정상
                createApiTaskDto("GET", "https://httpbin.org/status/500", null), // 서버 오류
                createApiTaskDto("GET", "https://httpbin.org/delay/1", null) // 정상 (지연)
        );

        // When - 메시지들을 스트림에 추가
        List<String> recordIds = new ArrayList<>();
        for (ApiTaskDto message : messages) {
            var dataMap = ApiTaskDtoConverter.toMap(message);
            var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
            var recordId = redisTemplate.opsForStream().add(record);
            if (recordId != null) {
                recordIds.add(recordId.getValue());
                System.out.println("Test: Sent mixed scenario message: " + message.uri());
            }
        }

        // Then - 오류가 있어도 시스템이 계속 동작하는지 확인
        await().atMost(Duration.ofSeconds(45)).pollInterval(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    var pendingMessages = redisTemplate.opsForStream().pending(
                            StreamConstants.STREAM_KEY,
                            StreamConstants.GROUP_NAME);

                    long remainingMessages = pendingMessages != null ? pendingMessages.getTotalPendingMessages() : 0;

                    System.out.println("Test: Error scenario test - Remaining pending messages: " +
                            remainingMessages + "/" + messages.size());

                    // 오류 메시지들도 ACK 처리되어 pending이 0이 되어야 함
                    assertThat(remainingMessages)
                            .as("오류가 있어도 모든 메시지가 처리되어야 함")
                            .isZero();
                });

        assertThat(recordIds).hasSize(messages.size());
    }

    /**
     * 테스트용 ApiTaskDto 생성 헬퍼 메서드
     */
    private ApiTaskDto createApiTaskDto(String method, String uri, Map<String, Object> body) {
        return ApiTaskDto.builder()
                .id((int) (Math.random() * 1000))
                .testcaseId((int) (Math.random() * 1000) + 1000)
                .resultId((int) (Math.random() * 1000) + 2000)
                .precondition("Advanced integration test precondition")
                .step(1)
                .attempt(1)
                .method(method)
                .uri(uri)
                .reqHeader(new LinkedMultiValueMap<>())
                .reqBody(body != null ? body : new HashMap<>())
                .reqQuery(new LinkedMultiValueMap<>())
                .reqPath(new HashMap<>())
                .statusCode(2) // 2XX 응답 기대
                .resHeader(new LinkedMultiValueMap<>())
                .resBody(new HashMap<>())
                .build();
    }
}
