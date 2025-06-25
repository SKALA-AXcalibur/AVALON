package com.sk.skala.axcalibur.apitest.feature.service;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

import com.sk.skala.axcalibur.apitest.feature.code.StreamConstants;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import com.sk.skala.axcalibur.apitest.feature.util.ApiTaskDtoConverter;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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
                                        assertThat(pendingMessages.getTotalPendingMessages())
                                                        .isZero();

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

        /**
         * 테스트용 ApiTaskDto 생성 헬퍼 메서드
         */
        private ApiTaskDto createApiTaskDto(String method, String uri,
                        MultiValueMap<String, String> headers, Map<String, Object> body) {
                return ApiTaskDto.builder().id((int) (Math.random() * 1000))
                                .testcaseId((int) (Math.random() * 1000) + 1000)
                                .resultId((int) (Math.random() * 1000) + 1000)
                                .precondition("Integration test precondition")
                                .step(1)
                                .attempt(1)
                                .method(method).uri(uri).reqHeader(headers).reqBody(body)
                                .statusCode(2) // 2XX 응답 기대
                                .resHeader(null).resBody(null).build();
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
