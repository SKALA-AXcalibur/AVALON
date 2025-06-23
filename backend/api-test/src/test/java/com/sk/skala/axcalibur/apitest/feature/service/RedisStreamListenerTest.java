package com.sk.skala.axcalibur.apitest.feature.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.skala.axcalibur.apitest.feature.code.StreamConstants;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import com.sk.skala.axcalibur.apitest.feature.util.ApiTaskDtoConverter;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
@DisplayName("Redis Stream Listener 테스트")
@TestPropertySource("logging.level.com.sk.skala.axcalibur.apitest.feature.service.RedisStreamListener=DEBUG")
class RedisStreamListenerTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private StreamOperations<String, Object, Object> streamOperations;

    private RedisStreamListener redisStreamListener;

    @BeforeEach
    void setUp() {
        redisStreamListener = new RedisStreamListener(RestClient.create(), redisTemplate);
        when(redisTemplate.opsForStream()).thenReturn(streamOperations);
    }

    @Test
    @DisplayName("GET 요청 메시지가 정상적으로 처리되어야 한다")
    void onMessage_shouldProcessGetRequestSuccessfully() {
        // Given
        var apiTaskDto = createApiTaskDto("GET", "http://httpbin.org/get", null, null);
        var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);

        assertThat(ApiTaskDtoConverter.fromMap(dataMap)).isEqualTo(apiTaskDto);
        MapRecord<String, String, String> message = createMapRecord("test-id", dataMap);

        // When
        redisStreamListener.onMessage(message);

        // Then
        verify(streamOperations).acknowledge(StreamConstants.GROUP_NAME, message);
    }

    @Test
    @DisplayName("POST 요청 메시지가 정상적으로 처리되어야 한다")
    void onMessage_shouldProcessPostRequestWithBodySuccessfully() {
        // Given
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("key", "value");
        requestBody.put("number", 123);

        ApiTaskDto apiTaskDto = createApiTaskDto("POST", "http://httpbin.org/post", null, requestBody);
        var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);

        assertThat(ApiTaskDtoConverter.fromMap(dataMap)).isEqualTo(apiTaskDto);
        MapRecord<String, String, String> message = createMapRecord("test-id", dataMap);

        // When
        redisStreamListener.onMessage(message);

        // Then
        verify(streamOperations).acknowledge(StreamConstants.GROUP_NAME, message);
    }

    @Test
    @DisplayName("헤더가 포함된 요청이 정상적으로 처리되어야 한다")
    void onMessage_shouldProcessRequestWithHeadersSuccessfully() {
        // Given
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer test-token");

        ApiTaskDto apiTaskDto = createApiTaskDto("GET", "http://httpbin.org/headers", headers, null);
        var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);

        assertThat(ApiTaskDtoConverter.fromMap(dataMap)).isEqualTo(apiTaskDto);
        MapRecord<String, String, String> message = createMapRecord("test-id", dataMap);

        // When
        redisStreamListener.onMessage(message);

        // Then
        verify(streamOperations).acknowledge(StreamConstants.GROUP_NAME, message);
    }

    @Test
    @DisplayName("잘못된 HTTP 메서드로 인한 RestClientResponseException가 발생해면 메시지를 ACK해야 한다")
    void onMessage_shouldAckOnRestClientResponseException() {
        // Given
        ApiTaskDto apiTaskDto = createApiTaskDto("INVALID_METHOD", "http://httpbin.org/get", null, null);
        var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);

        assertThat(ApiTaskDtoConverter.fromMap(dataMap)).isEqualTo(apiTaskDto);
        MapRecord<String, String, String> message = createMapRecord("test-id", dataMap);

        // When
        redisStreamListener.onMessage(message);

        // Then
        verify(streamOperations).acknowledge(StreamConstants.GROUP_NAME, message);
    }

    @Test
    @DisplayName("네트워크 오류가 발생하면 메시지를 ACK해야 한다")
    void onMessage_shouldAckOnResourceAccessException() {
        // Given - 접근할 수 없는 URL로 테스트
        ApiTaskDto apiTaskDto = createApiTaskDto("GET", "http://localhost:49999/test", null, null);
        var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);

        assertThat(ApiTaskDtoConverter.fromMap(dataMap)).isEqualTo(apiTaskDto);
        MapRecord<String, String, String> message = createMapRecord("test-id", dataMap);

        // When
        redisStreamListener.onMessage(message);

        // Then
        verify(streamOperations).acknowledge(StreamConstants.GROUP_NAME, message);
    }

    @Test
    @DisplayName("PUT 요청이 정상적으로 처리되어야 한다")
    void onMessage_shouldProcessPutRequestSuccessfully() {
        // Given
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("data", "updated");

        ApiTaskDto apiTaskDto = createApiTaskDto("PUT", "http://httpbin.org/put", null, requestBody);
        var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);

        assertThat(ApiTaskDtoConverter.fromMap(dataMap)).isEqualTo(apiTaskDto);
        MapRecord<String, String, String> message = createMapRecord("test-id", dataMap);

        // When
        redisStreamListener.onMessage(message);

        // Then
        verify(streamOperations).acknowledge(StreamConstants.GROUP_NAME, message);
    }

    @Test
    @DisplayName("DELETE 요청이 정상적으로 처리되어야 한다")
    void onMessage_shouldProcessDeleteRequestSuccessfully() {
        // Given
        ApiTaskDto apiTaskDto = createApiTaskDto("DELETE", "http://httpbin.org/delete", null, null);
        var dataMap = ApiTaskDtoConverter.toMap(apiTaskDto);

        assertThat(ApiTaskDtoConverter.fromMap(dataMap)).isEqualTo(apiTaskDto);
        MapRecord<String, String, String> message = createMapRecord("test-id", dataMap);

        // When
        redisStreamListener.onMessage(message);

        // Then
        verify(streamOperations).acknowledge(StreamConstants.GROUP_NAME, message);
    }

    /**
     * 테스트용 ApiTaskDto 생성 헬퍼 메서드
     */
    private ApiTaskDto createApiTaskDto(String method, String uri,
            MultiValueMap<String, String> headers,
            Map<String, Object> body) {
        return ApiTaskDto.builder()
                .id(1)
                .resultId(101)
                .precondition("테스트 사전조건")
                .step(1)
                .method(method)
                .uri(uri)
                .reqHeader(headers)
                .reqBody(body)
                .statusCode(2) // 2XX 응답 기대
                .resHeader(null)
                .resBody(null)
                .build();
    }

    /**
     * 테스트용 MapRecord 생성 헬퍼 메서드
     */
    private MapRecord<String, String, String> createMapRecord(String id, Map<String, String> data) {
        return MapRecord.create(StreamConstants.STREAM_KEY, data)
                .withId(RecordId.of(id));
    }
}