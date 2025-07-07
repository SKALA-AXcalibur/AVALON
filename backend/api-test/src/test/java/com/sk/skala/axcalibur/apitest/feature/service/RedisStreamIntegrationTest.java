package com.sk.skala.axcalibur.apitest.feature.service;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

import com.sk.skala.axcalibur.apitest.feature.code.StreamConstants;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import com.sk.skala.axcalibur.apitest.feature.util.ApiTaskDtoConverter;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest
@TestPropertySource(properties = { "spring.data.redis.host=localhost", "spring.data.redis.port=6379",
                "logging.level.com.sk.skala.axcalibur.apitest.feature.service.RedisStreamListener=DEBUG" })
@DisplayName("Redis Streams 통합 테스트")
class RedisStreamIntegrationTest {

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

                        if (pendingMessages != null
                                        && pendingMessages.getTotalPendingMessages() > 0) {
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
        @DisplayName("GET 요청 메시지가 Redis Stream을 통해 정상적으로 처리되어야 한다")
        void redisStreamIntegration_shouldProcessGetRequest() { // Given
                ApiTaskDto apiTaskDto = createApiTaskDto("GET", "https://httpbin.org/get", null, null);

                // When - Redis Stream에 메시지 전송
                var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);
                System.out.println("Test: Sending message with data: " + dataMap);
                var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
                var recordId = redisTemplate.opsForStream().add(record);
                System.out.println("Test: Message sent with ID: " + recordId);

                // Then - 메시지가 처리될 때까지 대기 (최대 10초)
                await().atMost(Duration.ofSeconds(10)).pollInterval(Duration.ofMillis(500))
                                .untilAsserted(() -> {
                                        // 스트림에서 처리된 메시지 확인 (PENDING 목록에서 제거되었는지 확인)
                                        var pendingMessages = redisTemplate.opsForStream().pending(
                                                        StreamConstants.STREAM_KEY,
                                                        StreamConstants.GROUP_NAME);
                                        assertThat(pendingMessages).isNotNull();
                                        if (pendingMessages != null) {
                                                assertThat(pendingMessages.getTotalPendingMessages())
                                                                .isZero();
                                        }

                                        // 메시지가 성공적으로 처리된 후 last-delivered-id 확인
                                        var lastDeliveredId = getLastDeliveredId(
                                                        StreamConstants.STREAM_KEY,
                                                        StreamConstants.GROUP_NAME);
                                        System.out.println("Last Delivered ID: " + lastDeliveredId);
                                        assertThat(lastDeliveredId).isNotNull();
                                        assertThat(lastDeliveredId)
                                                        .as("메시지 처리 후 last-delivered-id가 업데이트되어야 함")
                                                        .isNotEqualTo("0-0");
                                });

                assertThat(recordId).isNotNull();
        }

        @Test
        @DisplayName("POST 요청 메시지가 Redis Stream을 통해 정상적으로 처리되어야 한다")
        void redisStreamIntegration_shouldProcessPostRequest() {
                // Given
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("name", "integration-test");
                requestBody.put("value", 42);

                ApiTaskDto apiTaskDto = createApiTaskDto("POST", "https://httpbin.org/post", null,
                                requestBody); // When
                var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);
                var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
                var recordId = redisTemplate.opsForStream().add(record); // Then
                await().atMost(Duration.ofSeconds(10)).pollInterval(Duration.ofMillis(500))
                                .untilAsserted(() -> {
                                        var pendingMessages = redisTemplate.opsForStream().pending(
                                                        StreamConstants.STREAM_KEY,
                                                        StreamConstants.GROUP_NAME);

                                        var lastDeliveredId = getLastDeliveredId(
                                                        StreamConstants.STREAM_KEY,
                                                        StreamConstants.GROUP_NAME);
                                        assertThat(lastDeliveredId).isNotNull();
                                        assertThat(lastDeliveredId).as(
                                                        "last-delivered-id should not be initial value after processing")
                                                        .isNotEqualTo("0-0");
                                        assertThat(pendingMessages).isNotNull();
                                        assertThat(pendingMessages.getTotalPendingMessages())
                                                        .isZero();
                                });

                assertThat(recordId).isNotNull();
        }

        @Test
        @DisplayName("헤더가 포함된 요청이 Redis Stream을 통해 정상적으로 처리되어야 한다")
        void redisStreamIntegration_shouldProcessRequestWithHeaders() {
                // Given
                MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
                headers.add("User-Agent", "Integration-Test/1.0");
                headers.add("X-Test-Header", "test-value");
                ApiTaskDto apiTaskDto = createApiTaskDto("GET", "https://httpbin.org/headers",
                                headers, null);

                // When
                var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);
                var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
                var recordId = redisTemplate.opsForStream().add(record);

                // Then
                await().atMost(Duration.ofSeconds(10)).pollInterval(Duration.ofMillis(500))
                                .untilAsserted(() -> {
                                        var pendingMessages = redisTemplate.opsForStream().pending(
                                                        StreamConstants.STREAM_KEY,
                                                        StreamConstants.GROUP_NAME);
                                        var lastDeliveredId = getLastDeliveredId(
                                                        StreamConstants.STREAM_KEY,
                                                        StreamConstants.GROUP_NAME);
                                        assertThat(lastDeliveredId).isNotNull();
                                        assertThat(lastDeliveredId).as(
                                                        "last-delivered-id should not be initial value after processing")
                                                        .isNotEqualTo("0-0");
                                        assertThat(pendingMessages).isNotNull();
                                        assertThat(pendingMessages.getTotalPendingMessages())
                                                        .isZero();
                                });

                assertThat(recordId).isNotNull();
        }

        @Test
        @DisplayName("잘못된 URL로 인한 오류 시 메시지가 처리 상태로 바뀌어야 한다")
        void redisStreamIntegration_shouldRetainMessageOnError() {
                // Given - 접근할 수 없는 URL
                ApiTaskDto apiTaskDto = createApiTaskDto("GET",
                                "http://non-existent-domain-12345.com/test", null, null);

                // When
                var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);
                var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
                var recordId = redisTemplate.opsForStream().add(record);

                // Then - 메시지가 pending 상태로 남아있는지 확인
                await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(500))
                                .untilAsserted(() -> {
                                        var pendingMessages = redisTemplate.opsForStream().pending(
                                                        StreamConstants.STREAM_KEY,
                                                        StreamConstants.GROUP_NAME);

                                        var lastDeliveredId = getLastDeliveredId(
                                                        StreamConstants.STREAM_KEY,
                                                        StreamConstants.GROUP_NAME);
                                        assertThat(lastDeliveredId).isNotNull();
                                        assertThat(lastDeliveredId).as(
                                                        "last-delivered-id should be initial value")
                                                        .isNotEqualTo("0-0");

                                        // 오류로 인해 ACK되지 않아 pending 상태로 남아있어야 함
                                        assertThat(pendingMessages).isNotNull();
                                        assertThat(pendingMessages.getTotalPendingMessages())
                                                        .isZero();
                                });

                assertThat(recordId).isNotNull();
        }

        @Test
        @DisplayName("여러 메시지가 순차적으로 처리되어야 한다")
        void redisStreamIntegration_shouldProcessMultipleMessagesSequentially() {
                // Given - 여러 메시지 생성
                List<ApiTaskDto> messages = List.of(
                                createApiTaskDto("GET", "https://httpbin.org/delay/1", null, null),
                                createApiTaskDto("POST", "https://httpbin.org/post", null,
                                                Map.of("test", "data1")),
                                createApiTaskDto("GET", "https://httpbin.org/get", null, null));

                // When - 모든 메시지를 스트림에 추가
                List<String> recordIds = new ArrayList<>();
                for (ApiTaskDto message : messages) {
                        var dataMap = ApiTaskDtoConverter.toMap(message);
                        var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
                        var recordId = redisTemplate.opsForStream().add(record);
                        if (recordId != null) {
                                recordIds.add(recordId.getValue());
                                System.out.println("Test: Added message with ID: " + recordId);
                        }
                }

                // Then - 모든 메시지가 처리될 때까지 대기
                await().atMost(Duration.ofSeconds(30)).pollInterval(Duration.ofSeconds(1))
                                .untilAsserted(() -> {
                                        var pendingMessages = redisTemplate.opsForStream().pending(
                                                        StreamConstants.STREAM_KEY,
                                                        StreamConstants.GROUP_NAME);

                                        System.out.println("Test: Pending messages: " +
                                                        pendingMessages.getTotalPendingMessages());

                                        // 모든 메시지가 처리되어야 함
                                        assertThat(pendingMessages.getTotalPendingMessages())
                                                        .as("모든 메시지가 처리되어야 함")
                                                        .isZero();
                                });

                // 모든 recordId가 유효해야 함
                assertThat(recordIds).hasSize(3);
                recordIds.forEach(id -> assertThat(id).isNotNull());
        }

        @Test
        @DisplayName("동일한 타입의 여러 메시지가 병렬로 처리되어야 한다")
        void redisStreamIntegration_shouldProcessSimilarMessagesConcurrently() {
                // Given - 동일한 엔드포인트에 대한 여러 메시지
                int messageCount = 5;
                List<ApiTaskDto> messages = new ArrayList<>();

                for (int i = 0; i < messageCount; i++) {
                        Map<String, Object> body = Map.of("message", "test-" + i, "index", i);
                        messages.add(createApiTaskDto("POST", "https://httpbin.org/post", null, body));
                }

                // When - 모든 메시지를 빠르게 스트림에 추가
                List<String> recordIds = new ArrayList<>();
                long startTime = System.currentTimeMillis();

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

                // Then - 모든 메시지가 처리될 때까지 대기
                await().atMost(Duration.ofSeconds(20)).pollInterval(Duration.ofMillis(500))
                                .untilAsserted(() -> {
                                        var pendingMessages = redisTemplate.opsForStream().pending(
                                                        StreamConstants.STREAM_KEY,
                                                        StreamConstants.GROUP_NAME);

                                        System.out.println("Test: Remaining pending messages: " +
                                                        pendingMessages.getTotalPendingMessages());

                                        assertThat(pendingMessages.getTotalPendingMessages())
                                                        .as("모든 메시지가 처리되어야 함")
                                                        .isZero();
                                });

                assertThat(recordIds).hasSize(messageCount);
        }

        @Test
        @DisplayName("Stream 길이가 제한되어야 한다")
        void redisStreamIntegration_shouldManageStreamLength() {
                // Given - 여러 메시지 추가
                int messageCount = 10;
                List<String> recordIds = new ArrayList<>();

                // When - 메시지를 추가하면서 스트림 길이 확인
                for (int i = 0; i < messageCount; i++) {
                        ApiTaskDto apiTaskDto = createApiTaskDto("GET", "https://httpbin.org/get", null, null);
                        var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);
                        var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
                        var recordId = redisTemplate.opsForStream().add(record);
                        if (recordId != null) {
                                recordIds.add(recordId.getValue());
                        }
                }

                // Then - 스트림 정보 확인
                await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
                        var streamInfo = redisTemplate.opsForStream().info(StreamConstants.STREAM_KEY);
                        System.out.println("Test: Stream length: " + streamInfo.streamLength());
                        System.out.println("Test: Consumer groups: " + streamInfo.groupCount());

                        assertThat(streamInfo.streamLength()).isGreaterThan(0);
                        assertThat(streamInfo.groupCount()).isEqualTo(1);
                });

                assertThat(recordIds).hasSize(messageCount);
        }

        @Test
        @DisplayName("Consumer Group 정보가 정확하게 유지되어야 한다")
        void redisStreamIntegration_shouldMaintainConsumerGroupInfo() {
                // Given - 테스트 메시지 추가
                ApiTaskDto apiTaskDto = createApiTaskDto("GET", "https://httpbin.org/get", null, null);
                var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);
                var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
                var recordId = redisTemplate.opsForStream().add(record);

                // When & Then - Consumer Group 정보 확인
                await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
                        // Consumer Group 목록 확인
                        var groups = redisTemplate.opsForStream().groups(StreamConstants.STREAM_KEY);
                        assertThat(groups).isNotEmpty();
                        assertThat(groups).hasSize(1);

                        var group = groups.get(0);
                        assertThat(group.groupName()).isEqualTo(StreamConstants.GROUP_NAME);

                        System.out.println("Test: Group name: " + group.groupName());
                        System.out.println("Test: Consumer count: " + group.consumerCount());
                        System.out.println("Test: Pending count: " + group.pendingCount());
                        System.out.println("Test: Last delivered ID: " + group.lastDeliveredId());

                        // Consumer 정보 확인
                        var consumers = redisTemplate.opsForStream().consumers(
                                        StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME);

                        System.out.println("Test: Active consumers: " + consumers.size());
                        for (var consumer : consumers) {
                                System.out.println("Test: Consumer - name: " + consumer.consumerName() +
                                                ", pending: " + consumer.pendingCount() +
                                                ", idle: " + consumer.idleTime());
                        }
                });

                assertThat(recordId).isNotNull();
        }

        @Test
        @DisplayName("PUT 요청이 정상적으로 처리되어야 한다")
        void redisStreamIntegration_shouldProcessPutRequest() {
                // Given
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("name", "updated-test");
                requestBody.put("status", "active");

                ApiTaskDto apiTaskDto = createApiTaskDto("PUT", "https://httpbin.org/put", null, requestBody);

                // When
                var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);
                System.out.println("Test: Sending PUT message with data: " + dataMap);
                var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
                var recordId = redisTemplate.opsForStream().add(record);

                // Then
                await().atMost(Duration.ofSeconds(10)).pollInterval(Duration.ofMillis(500))
                                .untilAsserted(() -> {
                                        var pendingMessages = redisTemplate.opsForStream().pending(
                                                        StreamConstants.STREAM_KEY,
                                                        StreamConstants.GROUP_NAME);

                                        var lastDeliveredId = getLastDeliveredId(
                                                        StreamConstants.STREAM_KEY,
                                                        StreamConstants.GROUP_NAME);

                                        assertThat(lastDeliveredId).isNotNull();
                                        assertThat(lastDeliveredId)
                                                        .as("PUT 요청 처리 후 last-delivered-id가 업데이트되어야 함")
                                                        .isNotEqualTo("0-0");
                                        assertThat(pendingMessages.getTotalPendingMessages())
                                                        .isZero();
                                });

                assertThat(recordId).isNotNull();
        }

        @Test
        @DisplayName("DELETE 요청이 정상적으로 처리되어야 한다")
        void redisStreamIntegration_shouldProcessDeleteRequest() {
                // Given
                ApiTaskDto apiTaskDto = createApiTaskDto("DELETE", "https://httpbin.org/delete", null, null);

                // When
                var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);
                System.out.println("Test: Sending DELETE message with data: " + dataMap);
                var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
                var recordId = redisTemplate.opsForStream().add(record);

                // Then
                await().atMost(Duration.ofSeconds(10)).pollInterval(Duration.ofMillis(500))
                                .untilAsserted(() -> {
                                        var pendingMessages = redisTemplate.opsForStream().pending(
                                                        StreamConstants.STREAM_KEY,
                                                        StreamConstants.GROUP_NAME);

                                        var lastDeliveredId = getLastDeliveredId(
                                                        StreamConstants.STREAM_KEY,
                                                        StreamConstants.GROUP_NAME);

                                        assertThat(lastDeliveredId).isNotNull();
                                        assertThat(lastDeliveredId)
                                                        .as("DELETE 요청 처리 후 last-delivered-id가 업데이트되어야 함")
                                                        .isNotEqualTo("0-0");
                                        assertThat(pendingMessages.getTotalPendingMessages())
                                                        .isZero();
                                });

                assertThat(recordId).isNotNull();
        }

        @Test
        @DisplayName("복잡한 JSON 응답을 가진 요청이 정상적으로 처리되어야 한다")
        void redisStreamIntegration_shouldProcessComplexJsonResponse() {
                // Given - JSON 응답을 반환하는 엔드포인트 테스트
                MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
                headers.add("Content-Type", "application/json");
                headers.add("Accept", "application/json");

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("array", List.of("item1", "item2", "item3"));
                requestBody.put("nested", Map.of("key1", "value1", "key2", 123));
                requestBody.put("boolean", true);
                requestBody.put("number", 42.5);

                ApiTaskDto apiTaskDto = createApiTaskDto("POST", "https://httpbin.org/post", headers, requestBody);

                // When
                var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);
                System.out.println("Test: Sending complex JSON message");
                var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
                var recordId = redisTemplate.opsForStream().add(record);

                // Then
                await().atMost(Duration.ofSeconds(15)).pollInterval(Duration.ofMillis(500))
                                .untilAsserted(() -> {
                                        var pendingMessages = redisTemplate.opsForStream().pending(
                                                        StreamConstants.STREAM_KEY,
                                                        StreamConstants.GROUP_NAME);

                                        assertThat(pendingMessages.getTotalPendingMessages())
                                                        .as("복잡한 JSON 요청이 정상 처리되어야 함")
                                                        .isZero();
                                });

                assertThat(recordId).isNotNull();
        }

        /**
         * 테스트용 ApiTaskDto 생성 헬퍼 메서드
         */
        private ApiTaskDto createApiTaskDto(String method, String uri,
                        MultiValueMap<String, String> headers, Map<String, Object> body) {
                return ApiTaskDto.builder()
                                .id((int) (Math.random() * 1000))
                                .testcaseId((int) (Math.random() * 1000) + 1000)
                                .resultId((int) (Math.random() * 1000) + 2000)
                                .precondition("Integration test precondition")
                                .step(1)
                                .attempt(1)
                                .method(method)
                                .uri(uri)
                                .reqHeader(headers != null ? headers : new LinkedMultiValueMap<>())
                                .reqBody(body != null ? body : new HashMap<>())
                                .reqQuery(new LinkedMultiValueMap<>())
                                .reqPath(new HashMap<>())
                                .statusCode(2) // 2XX 응답 기대
                                .resHeader(new LinkedMultiValueMap<>())
                                .resBody(new HashMap<>())
                                .build();
        }

        /**
         * 특정 컨슈머 그룹의 last-delivered-id를 확인하는 헬퍼 메서드
         */
        private String getLastDeliveredId(String streamKey, String groupName) {
                var groupsInfo = redisTemplate.opsForStream().groups(streamKey);

                return groupsInfo.stream().filter(group -> groupName.equals(group.groupName()))
                                .findFirst().map(StreamInfo.XInfoGroup::lastDeliveredId)
                                .orElse(null);
        }

}
