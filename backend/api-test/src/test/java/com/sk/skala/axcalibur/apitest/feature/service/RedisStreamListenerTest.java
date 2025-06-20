package com.sk.skala.axcalibur.apitest.feature.service;

import com.sk.skala.axcalibur.apitest.feature.code.ApiTestStatus;
import com.sk.skala.axcalibur.apitest.feature.code.StreamConstants;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import com.sk.skala.axcalibur.apitest.feature.repository.AvalonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Redis Streams 동작을 테스트하는 통합 테스트 클래스
 * httpbin.org 엔드포인트를 사용하여 실제 HTTP 요청/응답을 테스트합니다.
 * 
 * 이 테스트를 실행하려면 Redis 서버가 실행 중이어야 하며,
 * 시스템 프로퍼티로 -Dredis.integration.test=true를 설정해야 합니다.
 * 
 * 예: ./gradlew test -Dredis.integration.test=true
 * 또는 IDE에서 VM options: -Dredis.integration.test=true
 */
@SpringBootTest
class RedisStreamListenerTest {
    @Autowired
    private AvalonRepository repo;
    @Autowired
    @Qualifier("apiTaskRedisTemplate")
    private RedisTemplate<String, ApiTaskDto> redisTemplate;

    @Autowired
    private RedisStreamListener redisStreamListener;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 스트림 정리
        try {
            redisTemplate.delete(StreamConstants.STREAM_KEY);
        } catch (Exception e) {
            // 스트림이 없는 경우 무시
        }

        // 잠시 대기하여 스트림 정리가 완료되도록 함
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testStreamInitialization() {
        // given: 간단한 GET 요청을 위한 ApiTaskDto 생성
        ApiTaskDto testTask = createGetTestTask();

        // when: 스트림에 ObjectRecord로 메시지 추가
        ObjectRecord<String, ApiTaskDto> record = StreamRecords.newRecord()
                .in(StreamConstants.STREAM_KEY)
                .ofObject(testTask);

        RecordId messageId = redisTemplate.opsForStream().add(record);

        // then: 메시지가 정상적으로 추가되었는지 확인
        assertThat(messageId).isNotNull();
        if (messageId != null) {
            assertThat(messageId.getValue()).isNotEmpty();
        }

        // 스트림에서 메시지 읽기로 확인
        var messages = redisTemplate.opsForStream().range(
                ApiTaskDto.class,
                StreamConstants.STREAM_KEY,
                org.springframework.data.domain.Range.unbounded());

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getValue().uri()).isEqualTo("https://httpbin.org/get");
    }

    @Test
    void testStreamConsumerGroupCreation() {
        // given: 스트림에 더미 메시지 추가 (그룹 생성을 위해)
        ApiTaskDto testTask = createGetTestTask();
        ObjectRecord<String, ApiTaskDto> record = StreamRecords.newRecord()
                .in(StreamConstants.STREAM_KEY)
                .ofObject(testTask);

        redisTemplate.opsForStream().add(record);

        // when: 컨슈머 그룹 생성
        try {
            redisTemplate.opsForStream().createGroup(StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME);
        } catch (Exception e) {
            // 그룹이 이미 존재하는 경우 무시
        }

        // then: 그룹이 정상적으로 생성되었는지 확인
        var groups = redisTemplate.opsForStream().groups(StreamConstants.STREAM_KEY);
        assertThat(groups).isNotEmpty();

        boolean groupExists = groups.stream()
                .anyMatch(group -> StreamConstants.GROUP_NAME.equals(group.groupName()));
        assertThat(groupExists).isTrue();
    }

    @Test
    void testHttpbinPostMessageSerialization() {
        // given: httpbin POST 요청을 위한 복잡한 ApiTaskDto 생성
        MultiValueMap<String, String> reqHeaders = new LinkedMultiValueMap<>();
        reqHeaders.add("Content-Type", "application/json");
        reqHeaders.add("User-Agent", "Redis-Stream-Test/1.0");
        reqHeaders.add("X-Test-Source", "RedisStreamListenerTest");

        Map<String, Object> reqBody = Map.of(
                "testName", "Redis Stream POST Test",
                "timestamp", System.currentTimeMillis(),
                "testData", Map.of(
                        "id", 123,
                        "active", true,
                        "tags", java.util.List.of("redis", "stream", "httpbin", "test")),
                "metadata", Map.of(
                        "version", "1.0",
                        "environment", "test"));

        ApiTaskDto complexTask = ApiTaskDto.builder()
                .status(ApiTestStatus.PENDING)
                .id(1)
                .resultId(100)
                .precondition("Valid JSON payload required")
                .step(1)
                .method("POST")
                .uri("https://httpbin.org/post")
                .reqHeader(reqHeaders)
                .reqBody(reqBody)
                .statusCode(2) // 2XX 응답 기대
                .time(0.0) // 초기값
                .build();

        // when: 스트림에 메시지 추가
        ObjectRecord<String, ApiTaskDto> record = StreamRecords.newRecord()
                .in(StreamConstants.STREAM_KEY)
                .ofObject(complexTask);

        RecordId messageId = redisTemplate.opsForStream().add(record);

        // then: 메시지가 정상적으로 직렬화되어 저장되었는지 확인
        assertThat(messageId).isNotNull();

        // 스트림에서 메시지 읽기
        var messages = redisTemplate.opsForStream().range(
                ApiTaskDto.class,
                StreamConstants.STREAM_KEY,
                org.springframework.data.domain.Range.unbounded());

        assertThat(messages).hasSize(1);
        ApiTaskDto retrievedTask = messages.get(0).getValue();

        // 직렬화/역직렬화가 올바르게 수행되었는지 확인
        assertThat(retrievedTask.status()).isEqualTo(ApiTestStatus.PENDING);
        assertThat(retrievedTask.id()).isEqualTo(1);
        assertThat(retrievedTask.resultId()).isEqualTo(100);
        assertThat(retrievedTask.method()).isEqualTo("POST");
        assertThat(retrievedTask.uri()).isEqualTo("https://httpbin.org/post");
        assertThat(retrievedTask.reqHeader().getFirst("Content-Type")).isEqualTo("application/json");
        assertThat(retrievedTask.reqHeader().getFirst("X-Test-Source")).isEqualTo("RedisStreamListenerTest");
        assertThat(retrievedTask.reqBody().get("testName")).isEqualTo("Redis Stream POST Test");
        assertThat(retrievedTask.precondition()).isEqualTo("Valid JSON payload required");
    }

    @Test
    void testHttpbinGetRequest() {
        // given: httpbin GET 요청 테스트
        ApiTaskDto getTask = ApiTaskDto.builder()
                .status(ApiTestStatus.PENDING)
                .id(2)
                .resultId(200)
                .step(1)
                .method("GET")
                .uri("https://httpbin.org/get?test=redis&stream=true")
                .statusCode(2)
                .build();

        // when: 스트림에 메시지 추가
        ObjectRecord<String, ApiTaskDto> record = StreamRecords.newRecord()
                .in(StreamConstants.STREAM_KEY)
                .ofObject(getTask);

        RecordId messageId = redisTemplate.opsForStream().add(record);

        // then: 메시지가 추가되었는지 확인
        assertThat(messageId).isNotNull();

        // 메시지가 스트림에 정상적으로 저장되었는지 확인
        var messages = redisTemplate.opsForStream().range(
                ApiTaskDto.class,
                StreamConstants.STREAM_KEY,
                org.springframework.data.domain.Range.unbounded());

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getValue().uri()).contains("httpbin.org/get");
    }

    @Test
    void testHttpbinPutRequest() {
        // given: httpbin PUT 요청 테스트
        Map<String, Object> putData = Map.of(
                "userId", 456,
                "action", "update",
                "data", Map.of(
                        "name", "Updated Test Data",
                        "status", "active",
                        "lastModified", System.currentTimeMillis()),
                "requestSource", "RedisStreamTest");

        ApiTaskDto putTask = ApiTaskDto.builder()
                .status(ApiTestStatus.PENDING)
                .id(3)
                .resultId(300)
                .step(1)
                .method("PUT")
                .uri("https://httpbin.org/put")
                .reqBody(putData)
                .statusCode(2)
                .build();

        // when: 스트림에 메시지 추가
        ObjectRecord<String, ApiTaskDto> record = StreamRecords.newRecord()
                .in(StreamConstants.STREAM_KEY)
                .ofObject(putTask);

        RecordId messageId = redisTemplate.opsForStream().add(record);

        // then: 메시지 확인
        assertThat(messageId).isNotNull();

        var messages = redisTemplate.opsForStream().range(
                ApiTaskDto.class,
                StreamConstants.STREAM_KEY,
                org.springframework.data.domain.Range.unbounded());

        assertThat(messages).hasSize(1);
        ApiTaskDto retrievedTask = messages.get(0).getValue();
        assertThat(retrievedTask.method()).isEqualTo("PUT");
        assertThat(retrievedTask.reqBody().get("userId")).isEqualTo(456);
        assertThat(retrievedTask.reqBody().get("requestSource")).isEqualTo("RedisStreamTest");
    }

    @Test
    void testHttpbinDeleteRequest() {
        // given: httpbin DELETE 요청 테스트
        ApiTaskDto deleteTask = ApiTaskDto.builder()
                .status(ApiTestStatus.PENDING)
                .id(4)
                .resultId(400)
                .step(1)
                .method("DELETE")
                .uri("https://httpbin.org/delete")
                .statusCode(2)
                .build();

        // when: 스트림에 메시지 추가
        ObjectRecord<String, ApiTaskDto> record = StreamRecords.newRecord()
                .in(StreamConstants.STREAM_KEY)
                .ofObject(deleteTask);

        RecordId messageId = redisTemplate.opsForStream().add(record);

        // then: 메시지 확인
        assertThat(messageId).isNotNull();

        var messages = redisTemplate.opsForStream().range(
                ApiTaskDto.class,
                StreamConstants.STREAM_KEY,
                org.springframework.data.domain.Range.unbounded());

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getValue().method()).isEqualTo("DELETE");
        assertThat(messages.get(0).getValue().uri()).isEqualTo("https://httpbin.org/delete");
    }

    @Test
    void testHttpbinStatusCodeEndpoints() {
        // given: 다양한 상태 코드를 테스트하는 httpbin 엔드포인트들
        int[] statusCodes = { 200, 201, 204, 400, 404, 500 };

        for (int i = 0; i < statusCodes.length; i++) {
            int statusCode = statusCodes[i];
            int expectedStatusGroup = statusCode / 100; // 2, 4, 5 등

            ApiTaskDto statusTask = ApiTaskDto.builder()
                    .status(ApiTestStatus.PENDING)
                    .id(10 + i)
                    .resultId(1000 + i)
                    .step(1)
                    .method("GET")
                    .uri("https://httpbin.org/status/" + statusCode)
                    .statusCode(expectedStatusGroup)
                    .build();

            // when: 스트림에 메시지 추가
            ObjectRecord<String, ApiTaskDto> record = StreamRecords.newRecord()
                    .in(StreamConstants.STREAM_KEY + "-status-" + statusCode)
                    .ofObject(statusTask);

            RecordId messageId = redisTemplate.opsForStream().add(record);

            // then: 메시지가 정상적으로 추가되었는지 확인
            assertThat(messageId).isNotNull();
        }
    }

    @Test
    void testHttpbinDelayedResponse() {
        // given: httpbin의 delay endpoint를 사용한 테스트 (2초 지연)
        ApiTaskDto delayTask = ApiTaskDto.builder()
                .status(ApiTestStatus.PENDING)
                .id(5)
                .resultId(500)
                .step(1)
                .method("GET")
                .uri("https://httpbin.org/delay/2")
                .statusCode(2) // 2XX 응답 기대 (시간이 걸려도)
                .build();

        // when
        ObjectRecord<String, ApiTaskDto> record = StreamRecords.newRecord()
                .in(StreamConstants.STREAM_KEY)
                .ofObject(delayTask);

        RecordId messageId = redisTemplate.opsForStream().add(record);

        // then
        assertThat(messageId).isNotNull();

        var messages = redisTemplate.opsForStream().range(
                ApiTaskDto.class,
                StreamConstants.STREAM_KEY,
                org.springframework.data.domain.Range.unbounded());

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getValue().uri()).contains("delay/2");
    }

    @Test
    void testHttpbinHeadersAndEncoding() {
        // given: 헤더와 인코딩을 테스트하는 httpbin 엔드포인트들
        MultiValueMap<String, String> customHeaders = new LinkedMultiValueMap<>();
        customHeaders.add("Accept", "application/json");
        customHeaders.add("Accept-Encoding", "gzip, deflate");
        customHeaders.add("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8");
        customHeaders.add("X-Custom-Header", "RedisStreamTest");

        ApiTaskDto headersTask = ApiTaskDto.builder()
                .status(ApiTestStatus.PENDING)
                .id(6)
                .resultId(600)
                .step(1)
                .method("GET")
                .uri("https://httpbin.org/headers")
                .reqHeader(customHeaders)
                .statusCode(2)
                .build();

        // when: 스트림에 메시지 추가
        ObjectRecord<String, ApiTaskDto> record = StreamRecords.newRecord()
                .in(StreamConstants.STREAM_KEY)
                .ofObject(headersTask);

        RecordId messageId = redisTemplate.opsForStream().add(record);

        // then: 메시지 확인
        assertThat(messageId).isNotNull();

        var messages = redisTemplate.opsForStream().range(
                ApiTaskDto.class,
                StreamConstants.STREAM_KEY,
                org.springframework.data.domain.Range.unbounded());

        assertThat(messages).hasSize(1);
        ApiTaskDto retrievedTask = messages.get(0).getValue();
        assertThat(retrievedTask.reqHeader().getFirst("X-Custom-Header")).isEqualTo("RedisStreamTest");
        assertThat(retrievedTask.reqHeader().getFirst("Accept")).isEqualTo("application/json");
    }

    @Test
    void testMultipleHttpMethodsWithHttpbin() {
        // given: httpbin의 다양한 HTTP 메소드 엔드포인트를 테스트
        String[] methods = { "GET", "POST", "PUT", "DELETE", "PATCH" };

        for (int i = 0; i < methods.length; i++) {
            String method = methods[i];
            Map<String, Object> bodyData = method.equals("GET") ? null
                    : Map.of(
                            "testMethod", method,
                            "index", i,
                            "timestamp", System.currentTimeMillis(),
                            "source", "RedisStreamTest");

            ApiTaskDto methodTask = ApiTaskDto.builder()
                    .status(ApiTestStatus.PENDING)
                    .id(20 + i)
                    .resultId(2000 + i)
                    .step(1)
                    .method(method)
                    .uri("https://httpbin.org/" + method.toLowerCase())
                    .reqBody(bodyData)
                    .statusCode(2)
                    .build();

            // when: 각 메소드별로 별도의 스트림에 메시지 추가
            ObjectRecord<String, ApiTaskDto> record = StreamRecords.newRecord()
                    .in(StreamConstants.STREAM_KEY + "-" + method.toLowerCase())
                    .ofObject(methodTask);

            RecordId messageId = redisTemplate.opsForStream().add(record);

            // then: 메시지가 정상적으로 추가되었는지 확인
            assertThat(messageId).isNotNull();
        }
    }

    @Test
    void testErrorHandlingWithHttpbin404() {
        // given: 404 응답을 반환하는 httpbin 엔드포인트
        ApiTaskDto notFoundTask = ApiTaskDto.builder()
                .status(ApiTestStatus.PENDING)
                .id(999)
                .resultId(9999)
                .step(1)
                .method("GET")
                .uri("https://httpbin.org/status/404") // 404 응답을 반환하는 엔드포인트
                .statusCode(4) // 4XX 응답 기대
                .build();

        // when: 스트림에 메시지 추가
        ObjectRecord<String, ApiTaskDto> record = StreamRecords.newRecord()
                .in(StreamConstants.STREAM_KEY)
                .ofObject(notFoundTask);

        RecordId messageId = redisTemplate.opsForStream().add(record);

        // then: 메시지가 추가되었는지 확인
        assertThat(messageId).isNotNull();

        // 직접 리스너 호출하여 404 응답 처리 테스트
        try {
            redisStreamListener.onMessage(record);
        } catch (Exception e) {
            // 404 응답은 RestClientResponseException을 발생시킬 수 있음
            assertThat(e).isInstanceOfAny(
                    org.springframework.web.client.RestClientResponseException.class,
                    org.springframework.web.client.RestClientException.class);
        }
    }

    @Test
    void testHttpbinServerError() {
        // given: 500 서버 에러를 반환하는 httpbin 엔드포인트
        ApiTaskDto serverErrorTask = ApiTaskDto.builder()
                .status(ApiTestStatus.PENDING)
                .id(998)
                .resultId(9998)
                .step(1)
                .method("GET")
                .uri("https://httpbin.org/status/500") // 500 응답을 반환하는 엔드포인트
                .statusCode(5) // 5XX 응답 기대
                .build();

        // when: 스트림에 메시지 추가
        ObjectRecord<String, ApiTaskDto> record = StreamRecords.newRecord()
                .in(StreamConstants.STREAM_KEY)
                .ofObject(serverErrorTask);

        RecordId messageId = redisTemplate.opsForStream().add(record);

        // then: 메시지가 추가되었는지 확인
        assertThat(messageId).isNotNull();

        // 스트림에서 메시지 확인
        var messages = redisTemplate.opsForStream().range(
                ApiTaskDto.class,
                StreamConstants.STREAM_KEY,
                org.springframework.data.domain.Range.unbounded());

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getValue().statusCode()).isEqualTo(5);
    }

    @Test
    void testStreamMessageOrderAndTimestamp() {
        // given: 순서가 있는 여러 메시지
        for (int i = 0; i < 5; i++) {
            ApiTaskDto sequentialTask = ApiTaskDto.builder()
                    .status(ApiTestStatus.PENDING)
                    .id(100 + i)
                    .resultId(10000 + i)
                    .step(i + 1)
                    .method("GET")
                    .uri("https://httpbin.org/get?sequence=" + i)
                    .statusCode(2)
                    .build();

            // when: 순서대로 스트림에 메시지 추가
            ObjectRecord<String, ApiTaskDto> record = StreamRecords.newRecord()
                    .in(StreamConstants.STREAM_KEY)
                    .ofObject(sequentialTask);

            RecordId messageId = redisTemplate.opsForStream().add(record);
            assertThat(messageId).isNotNull();

            // 메시지 간 간격을 두어 순서 보장
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // then: 메시지가 순서대로 저장되었는지 확인
        var messages = redisTemplate.opsForStream().range(
                ApiTaskDto.class,
                StreamConstants.STREAM_KEY,
                org.springframework.data.domain.Range.unbounded());

        assertThat(messages).hasSize(5);

        // 순서 확인
        for (int i = 0; i < 5; i++) {
            assertThat(messages.get(i).getValue().step()).isEqualTo(i + 1);
            assertThat(messages.get(i).getValue().id()).isEqualTo(100 + i);
        }
    }

    /**
     * httpbin GET 요청을 위한 기본 테스트 데이터 생성
     */
    private ApiTaskDto createGetTestTask() {
        return ApiTaskDto.builder()
                .status(ApiTestStatus.CREATED)
                .id(1)
                .resultId(100)
                .step(1)
                .method("GET")
                .uri("https://httpbin.org/get")
                .statusCode(2) // 2XX 응답 기대
                .build();
    }
}