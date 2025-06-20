package com.sk.skala.axcalibur.apitest.feature.dto.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.skala.axcalibur.apitest.feature.code.ApiTestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("ApiTaskDto 직렬화/역직렬화 테스트")
public class ApiTaskDtoTest {

    private ObjectMapper objectMapper;
    private ApiTaskDto testDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // 테스트용 MultiValueMap과 Map 데이터 준비
        MultiValueMap<String, String> reqHeader = new LinkedMultiValueMap<>();
        reqHeader.add("Content-Type", "application/json");
        reqHeader.add("Authorization", "Bearer token123");

        MultiValueMap<String, String> resHeader = new LinkedMultiValueMap<>();
        resHeader.add("Content-Type", "application/json");

        Map<String, Object> reqBody = new HashMap<>();
        reqBody.put("name", "test");
        reqBody.put("age", 25);
        reqBody.put("active", true);

        Map<String, Object> resBody = new HashMap<>();
        resBody.put("id", 1);
        resBody.put("status", "success");
        resBody.put("data", Map.of("result", "ok"));

        testDto = ApiTaskDto.builder()
                .status(ApiTestStatus.RUNNING)
                .id(100)
                .resultId(200)
                .precondition("사전조건 테스트")
                .step(1)
                .method("POST")
                .uri("/api/test")
                .reqHeader(reqHeader)
                .reqBody(reqBody)
                .statusCode(2)
                .resHeader(resHeader)
                .resBody(resBody)
                .time(1.5)
                .build();
    }

    @Test
    @DisplayName("ApiTaskDto JSON 직렬화 테스트")
    void testSerialization() throws JsonProcessingException {
        // when
        String json = objectMapper.writeValueAsString(testDto);

        // then
        assertThat(json).isNotNull();
        assertThat(json).contains("\"status\":\"RUNNING\"");
        assertThat(json).contains("\"id\":100");
        assertThat(json).contains("\"resultId\":200");
        assertThat(json).contains("\"precondition\":\"사전조건 테스트\"");
        assertThat(json).contains("\"step\":1");
        assertThat(json).contains("\"method\":\"POST\"");
        assertThat(json).contains("\"uri\":\"/api/test\"");
        assertThat(json).contains("\"statusCode\":2");
        assertThat(json).contains("\"time\":1.5");

        System.out.println("직렬화된 JSON:");
        System.out.println(json);
    }

    @Test
    @DisplayName("ApiTaskDto JSON 역직렬화 테스트")
    void testDeserialization() throws JsonProcessingException {
        // given
        String json = """
                {
                    "status": "PENDING",
                    "id": 101,
                    "resultId": 201,
                    "precondition": "사전조건 테스트2",
                    "step": 2,
                    "method": "GET",
                    "uri": "/api/test2",
                    "reqHeader": {
                        "Accept": ["application/json"],
                        "User-Agent": ["test-agent"]
                    },
                    "reqBody": {
                        "query": "test",
                        "limit": 10
                    },
                    "statusCode": 2,
                    "resHeader": {
                        "Content-Type": ["application/json"]
                    },
                    "resBody": {
                        "success": true,
                        "message": "OK"
                    },
                    "time": 2.3
                }
                """;

        // when
        ApiTaskDto deserializedDto = objectMapper.readValue(json, ApiTaskDto.class);

        // then
        assertAll(
                () -> assertThat(deserializedDto).isNotNull(),
                () -> assertThat(deserializedDto.status()).isEqualTo(ApiTestStatus.PENDING),
                () -> assertThat(deserializedDto.id()).isEqualTo(101),
                () -> assertThat(deserializedDto.resultId()).isEqualTo(201),
                () -> assertThat(deserializedDto.precondition()).isEqualTo("사전조건 테스트2"),
                () -> assertThat(deserializedDto.step()).isEqualTo(2),
                () -> assertThat(deserializedDto.method()).isEqualTo("GET"),
                () -> assertThat(deserializedDto.uri()).isEqualTo("/api/test2"),
                () -> assertThat(deserializedDto.statusCode()).isEqualTo(2),
                () -> assertThat(deserializedDto.time()).isEqualTo(2.3),
                () -> assertThat(deserializedDto.reqHeader()).isNotNull(),
                () -> assertThat(deserializedDto.reqBody()).isNotNull(),
                () -> assertThat(deserializedDto.resHeader()).isNotNull(),
                () -> assertThat(deserializedDto.resBody()).isNotNull());

        System.out.println("역직렬화된 DTO:");
        System.out.println(deserializedDto);
    }

    @Test
    @DisplayName("직렬화 후 역직렬화 일관성 테스트")
    void testSerializationDeserializationConsistency() throws JsonProcessingException {
        // when
        String json = objectMapper.writeValueAsString(testDto);
        ApiTaskDto deserializedDto = objectMapper.readValue(json, ApiTaskDto.class);

        // then
        assertAll(
                () -> assertThat(deserializedDto.status()).isEqualTo(testDto.status()),
                () -> assertThat(deserializedDto.id()).isEqualTo(testDto.id()),
                () -> assertThat(deserializedDto.resultId()).isEqualTo(testDto.resultId()),
                () -> assertThat(deserializedDto.precondition()).isEqualTo(testDto.precondition()),
                () -> assertThat(deserializedDto.step()).isEqualTo(testDto.step()),
                () -> assertThat(deserializedDto.method()).isEqualTo(testDto.method()),
                () -> assertThat(deserializedDto.uri()).isEqualTo(testDto.uri()),
                () -> assertThat(deserializedDto.statusCode()).isEqualTo(testDto.statusCode()),
                () -> assertThat(deserializedDto.time()).isEqualTo(testDto.time()));
    }

    @Test
    @DisplayName("null 값이 포함된 DTO 직렬화/역직렬화 테스트")
    void testNullValuesSerializationDeserialization() throws JsonProcessingException {
        // given - nullable 필드들을 null로 설정
        ApiTaskDto dtoWithNulls = ApiTaskDto.builder()
                .status(ApiTestStatus.SUCCESS)
                .id(103)
                .resultId(203)
                .precondition(null) // nullable
                .step(3)
                .method("DELETE")
                .uri("/api/delete")
                .reqHeader(null) // nullable
                .reqBody(null) // nullable
                .statusCode(2)
                .resHeader(null) // nullable
                .resBody(null) // nullable
                .time(null) // nullable
                .build();

        // when
        String json = objectMapper.writeValueAsString(dtoWithNulls);
        ApiTaskDto deserializedDto = objectMapper.readValue(json, ApiTaskDto.class);

        // then
        assertAll(
                () -> assertThat(deserializedDto.status()).isEqualTo(ApiTestStatus.SUCCESS),
                () -> assertThat(deserializedDto.id()).isEqualTo(103),
                () -> assertThat(deserializedDto.resultId()).isEqualTo(203),
                () -> assertThat(deserializedDto.precondition()).isNull(),
                () -> assertThat(deserializedDto.step()).isEqualTo(3),
                () -> assertThat(deserializedDto.method()).isEqualTo("DELETE"),
                () -> assertThat(deserializedDto.uri()).isEqualTo("/api/delete"),
                () -> assertThat(deserializedDto.reqHeader()).isNull(),
                () -> assertThat(deserializedDto.reqBody()).isNull(),
                () -> assertThat(deserializedDto.statusCode()).isEqualTo(2),
                () -> assertThat(deserializedDto.resHeader()).isNull(),
                () -> assertThat(deserializedDto.resBody()).isNull(),
                () -> assertThat(deserializedDto.time()).isNull());

        System.out.println("null 값을 포함한 JSON:");
        System.out.println(json);
    }

    @Test
    @DisplayName("ApiTestStatus Enum 직렬화/역직렬화 테스트")
    void testApiTestStatusSerialization() throws JsonProcessingException {
        // given
        for (ApiTestStatus status : ApiTestStatus.values()) {
            ApiTaskDto dto = ApiTaskDto.builder()
                    .status(status)
                    .id(1)
                    .resultId(1)
                    .step(1)
                    .method("GET")
                    .uri("/test")
                    .statusCode(2)
                    .build();

            // when
            String json = objectMapper.writeValueAsString(dto);
            ApiTaskDto deserializedDto = objectMapper.readValue(json, ApiTaskDto.class);

            // then
            assertThat(deserializedDto.status()).isEqualTo(status);
        }
    }
}
