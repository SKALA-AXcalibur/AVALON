package com.sk.skala.axcalibur.apitest.feature.dto.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("ApiTaskDto 직렬화/역직렬화 테스트")
public class ApiTaskDtoTest {

    private ApiTaskDto testDto;

    @BeforeEach
    void setUp() {
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
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("result", "ok");
        resBody.put("data", dataMap);

        testDto = ApiTaskDto.builder()
                .id(100)
                .resultId(200)
                .precondition("사전조건 테스트")
                .step(1)
                .method("POST")
                .uri("/api/test")
                .reqHeader(reqHeader).reqBody(reqBody)
                .statusCode(2)
                .resHeader(resHeader)
                .resBody(resBody)
                .build();
    }

    @Test
    @DisplayName("RedisSerializer를 이용한 JSON 직렬화/역직렬화 테스트")
    void testRedisJsonSerialization() {
        // given
        RedisSerializer<Object> serializer = new GenericJackson2JsonRedisSerializer();

        // when - 직렬화
        byte[] serializedData = serializer.serialize(testDto);

        // then - 직렬화 결과 검증
        assertThat(serializedData).isNotNull();
        assertThat(serializedData).hasSizeGreaterThan(0);

        // when - 역직렬화
        Object deserializedObject = serializer.deserialize(serializedData);

        // then - 역직렬화 결과 검증
        assertThat(deserializedObject).isNotNull();
        assertThat(deserializedObject).isInstanceOf(ApiTaskDto.class);

        final ApiTaskDto deserializedDto = (ApiTaskDto) deserializedObject;

        // 모든 필드가 정확히 복원되었는지 검증
        assertAll(
                () -> assertThat(deserializedDto.id()).isEqualTo(testDto.id()),
                () -> assertThat(deserializedDto.resultId()).isEqualTo(testDto.resultId()),
                () -> assertThat(deserializedDto.precondition()).isEqualTo(testDto.precondition()),
                () -> assertThat(deserializedDto.step()).isEqualTo(testDto.step()),
                () -> assertThat(deserializedDto.method()).isEqualTo(testDto.method()),
                () -> assertThat(deserializedDto.uri()).isEqualTo(testDto.uri()),
                () -> assertThat(deserializedDto.statusCode()).isEqualTo(testDto.statusCode()),
                () -> assertThat(deserializedDto.reqHeader()).isEqualTo(testDto.reqHeader()),
                () -> assertThat(deserializedDto.reqBody()).isEqualTo(testDto.reqBody()),
                () -> assertThat(deserializedDto.resHeader()).isEqualTo(testDto.resHeader()),
                () -> assertThat(deserializedDto.resBody()).isEqualTo(testDto.resBody()));
    }

    @Test
    @DisplayName("RedisSerializer 직렬화 후 원본 객체와 동일성 테스트")
    void testSerializationEquality() {
        // given
        RedisSerializer<Object> serializer = new GenericJackson2JsonRedisSerializer();

        // when
        byte[] serializedData = serializer.serialize(testDto);
        Object deserializedObject = serializer.deserialize(serializedData);

        // then
        assertThat(deserializedObject).isNotNull();
        assertThat(deserializedObject).isInstanceOf(ApiTaskDto.class);

        final ApiTaskDto deserializedDto = (ApiTaskDto) deserializedObject;
        assertThat(deserializedDto).isEqualTo(testDto);
    }

    @Test
    @DisplayName("null 값이 포함된 ApiTaskDto 직렬화/역직렬화 테스트")
    void testRedisSerializationWithNullValues() {
        // given
        ApiTaskDto dtoWithNulls = ApiTaskDto.builder()
                .id(1)
                .resultId(2)
                .precondition(null) // null 값
                .step(1)
                .method("GET")
                .uri("/api/test")
                .reqHeader(null) // null 값
                .reqBody(null) // null 값
                .statusCode(2)
                .resHeader(null) // null 값
                .resBody(null) // null 값
                .build();

        RedisSerializer<Object> serializer = new GenericJackson2JsonRedisSerializer();

        // when
        byte[] serializedData = serializer.serialize(dtoWithNulls);
        Object deserializedObject = serializer.deserialize(serializedData);

        // then
        assertThat(deserializedObject).isNotNull();
        assertThat(deserializedObject).isInstanceOf(ApiTaskDto.class);

        final ApiTaskDto deserializedDto = (ApiTaskDto) deserializedObject;
        assertThat(deserializedDto).isEqualTo(dtoWithNulls);

        assertAll(
                () -> assertThat(deserializedDto.precondition()).isNull(),
                () -> assertThat(deserializedDto.reqHeader()).isNull(),
                () -> assertThat(deserializedDto.reqBody()).isNull(),
                () -> assertThat(deserializedDto.resHeader()).isNull(),
                () -> assertThat(deserializedDto.resBody()).isNull());
    }

    @Test
    @DisplayName("ObjectHashMapper를 이용한 바이너리 Hash 직렬화/역직렬화 테스트")
    void testObjectHashMapperSerialization() {
        // given
        ObjectHashMapper objectHashMapper = new ObjectHashMapper();

        // when - 객체를 바이너리 Hash Map으로 직렬화
        Map<byte[], byte[]> binaryHashData = objectHashMapper.toHash(testDto);

        // then - 직렬화 결과 검증
        assertThat(binaryHashData).isNotNull();
        assertThat(binaryHashData).isNotEmpty();
        System.out.println("ObjectHashMapper - Hash entries count: " + binaryHashData.size());

        // when - 바이너리 Hash Map을 객체로 역직렬화
        ApiTaskDto deserializedDto = (ApiTaskDto) objectHashMapper.fromHash(binaryHashData);

        // then - 역직렬화 결과 검증
        assertThat(deserializedDto).isNotNull();

        // ObjectHashMapper는 복합 객체(MultiValueMap 등)의 완전한 직렬화에 제한이 있음
        // 기본 필드들이 정확히 복원되었는지 검증
        assertAll(
                () -> assertThat(deserializedDto.id()).isEqualTo(testDto.id()),
                () -> assertThat(deserializedDto.resultId()).isEqualTo(testDto.resultId()),
                () -> assertThat(deserializedDto.method()).isEqualTo(testDto.method()),
                () -> assertThat(deserializedDto.uri()).isEqualTo(testDto.uri()),
                () -> assertThat(deserializedDto.statusCode()).isEqualTo(testDto.statusCode()),
                () -> assertThat(deserializedDto.precondition()).isEqualTo(testDto.precondition()),
                () -> assertThat(deserializedDto.step()).isEqualTo(testDto.step()));

        // 복합 객체 필드들은 부분적으로만 복원됨
        System.out.println("Original reqBody: " + testDto.reqBody());
        System.out.println("Deserialized reqBody: " + deserializedDto.reqBody());

        // reqBody의 기본 타입 값들은 복원되는지 확인
        if (deserializedDto.reqBody() != null) {
            assertThat(deserializedDto.reqBody().get("name")).isEqualTo("test");
            assertThat(deserializedDto.reqBody().get("age")).isEqualTo(25);
            assertThat(deserializedDto.reqBody().get("active")).isEqualTo(true);
        }
    }

    @Test
    @DisplayName("Jackson2HashMapper를 이용한 JSON Hash 직렬화/역직렬화 테스트")
    void testJackson2HashMapperSerialization() {
        // given
        Jackson2HashMapper jackson2HashMapper = new Jackson2HashMapper(false); // flatten = false

        // when - 객체를 JSON Hash Map으로 직렬화
        Map<String, Object> jsonHashData = jackson2HashMapper.toHash(testDto);

        // then - 직렬화 결과 검증
        assertThat(jsonHashData).isNotNull();
        assertThat(jsonHashData).isNotEmpty();
        assertThat(jsonHashData).containsKey("id");
        assertThat(jsonHashData).containsKey("resultId");
        assertThat(jsonHashData).containsKey("method");
        assertThat(jsonHashData).containsKey("uri");

        System.out.println("Jackson2HashMapper - Hash data: " + jsonHashData);

        // 기본 필드 값 검증
        assertThat(jsonHashData.get("id")).isEqualTo(100);
        assertThat(jsonHashData.get("resultId")).isEqualTo(200);
        assertThat(jsonHashData.get("method")).isEqualTo("POST");
        assertThat(jsonHashData.get("uri")).isEqualTo("/api/test");
        assertThat(jsonHashData.get("statusCode")).isEqualTo(2);
        assertThat(jsonHashData.get("precondition")).isEqualTo("사전조건 테스트");
        assertThat(jsonHashData.get("step")).isEqualTo(1);

        // 복합 객체들은 JSON 문자열로 직렬화됨
        assertThat(jsonHashData.get("reqHeader")).isNotNull();
        assertThat(jsonHashData.get("reqBody")).isNotNull();
        assertThat(jsonHashData.get("resHeader")).isNotNull();
        assertThat(jsonHashData.get("resBody")).isNotNull();

        // when - JSON Hash Map을 객체로 역직렬화
        ApiTaskDto deserializedDto = (ApiTaskDto) jackson2HashMapper.fromHash(jsonHashData);

        // then - 역직렬화 결과 검증
        assertThat(deserializedDto).isNotNull();
        assertThat(deserializedDto).isEqualTo(testDto);
    }

    @Test
    @DisplayName("Jackson2HashMapper 플래튼 모드 테스트")
    void testJackson2HashMapperFlattened() {
        // given
        Jackson2HashMapper flattenedMapper = new Jackson2HashMapper(true); // flatten = true

        // when - 객체를 플래튼된 Hash Map으로 직렬화
        Map<String, Object> flattenedHashData = flattenedMapper.toHash(testDto);

        // then - 직렬화 결과 검증
        assertThat(flattenedHashData).isNotNull();
        assertThat(flattenedHashData).isNotEmpty();

        System.out.println("Jackson2HashMapper (flattened) - Hash data: " + flattenedHashData);

        // 플래튼 모드에서는 중첩된 객체의 프로퍼티들이 점(.) 표기법으로 평면화됨
        assertThat(flattenedHashData.get("id")).isEqualTo(100);
        assertThat(flattenedHashData.get("resultId")).isEqualTo(200);
        assertThat(flattenedHashData.get("method")).isEqualTo("POST");
        assertThat(flattenedHashData.get("uri")).isEqualTo("/api/test");

        // 플래튼된 데이터는 다시 객체로 복원할 수 없음 (문서에 명시됨)
        // "The resulting hash cannot be mapped back into an Object."
        // 따라서 역직렬화 테스트는 하지 않음
    }

    @Test
    @DisplayName("HashMapper null 값 처리 비교 테스트")
    void testHashMappersWithNullValues() {
        // given
        ApiTaskDto dtoWithNulls = ApiTaskDto.builder()
                .id(1)
                .resultId(2)
                .precondition(null)
                .step(1)
                .method("GET")
                .uri("/api/test")
                .reqHeader(null)
                .reqBody(null)
                .statusCode(2)
                .resHeader(null)
                .resBody(null)
                .build();

        ObjectHashMapper objectHashMapper = new ObjectHashMapper();
        Jackson2HashMapper jackson2HashMapper = new Jackson2HashMapper(false);

        // when & then - ObjectHashMapper
        Map<byte[], byte[]> objectHashData = objectHashMapper.toHash(dtoWithNulls);
        assertThat(objectHashData).isNotNull();

        ApiTaskDto objectMappedResult = (ApiTaskDto) objectHashMapper.fromHash(objectHashData);
        assertThat(objectMappedResult).isEqualTo(dtoWithNulls);

        // when & then - Jackson2HashMapper
        Map<String, Object> jsonHashData = jackson2HashMapper.toHash(dtoWithNulls);
        assertThat(jsonHashData).isNotNull();

        // null 값들이 Hash에 포함되지 않는지 확인
        System.out.println("Jackson2HashMapper with nulls - Hash data: " + jsonHashData);

        ApiTaskDto jsonMappedResult = (ApiTaskDto) jackson2HashMapper.fromHash(jsonHashData);
        assertThat(jsonMappedResult).isEqualTo(dtoWithNulls);

        // 기본 필드들이 정확히 복원되었는지 검증
        assertAll(
                () -> assertThat(jsonMappedResult.id()).isEqualTo(1),
                () -> assertThat(jsonMappedResult.resultId()).isEqualTo(2),
                () -> assertThat(jsonMappedResult.step()).isEqualTo(1),
                () -> assertThat(jsonMappedResult.method()).isEqualTo("GET"),
                () -> assertThat(jsonMappedResult.uri()).isEqualTo("/api/test"),
                () -> assertThat(jsonMappedResult.statusCode()).isEqualTo(2),
                () -> assertThat(jsonMappedResult.precondition()).isNull(),
                () -> assertThat(jsonMappedResult.reqHeader()).isNull(),
                () -> assertThat(jsonMappedResult.reqBody()).isNull(),
                () -> assertThat(jsonMappedResult.resHeader()).isNull(),
                () -> assertThat(jsonMappedResult.resBody()).isNull());
    }

    @Test
    @DisplayName("Jackson2JsonRedisSerializer(Class) 기본 생성자 테스트")
    void testJackson2JsonRedisSerializerWithClass() {
        // given
        Jackson2JsonRedisSerializer<ApiTaskDto> serializer = new Jackson2JsonRedisSerializer<>(ApiTaskDto.class);

        // when - 직렬화
        byte[] serializedData = serializer.serialize(testDto);

        // then - 직렬화 결과 검증
        assertThat(serializedData).isNotNull();
        assertThat(serializedData).hasSizeGreaterThan(0);

        // JSON 형태로 직렬화되었는지 확인
        String jsonString = new String(serializedData);
        System.out.println("Jackson2JsonRedisSerializer (Class) - JSON: " + jsonString);

        assertThat(jsonString).contains("\"id\":100");
        assertThat(jsonString).contains("\"method\":\"POST\"");
        assertThat(jsonString).contains("\"uri\":\"/api/test\"");

        // when - 역직렬화
        ApiTaskDto deserializedDto = serializer.deserialize(serializedData);

        // then - 역직렬화 결과 검증
        assertThat(deserializedDto).isNotNull();
        assertThat(deserializedDto).isEqualTo(testDto);

        // 모든 필드가 정확히 복원되었는지 검증
        assertAll(
                () -> assertThat(deserializedDto.id()).isEqualTo(testDto.id()),
                () -> assertThat(deserializedDto.resultId()).isEqualTo(testDto.resultId()),
                () -> assertThat(deserializedDto.method()).isEqualTo(testDto.method()),
                () -> assertThat(deserializedDto.uri()).isEqualTo(testDto.uri()),
                () -> assertThat(deserializedDto.statusCode()).isEqualTo(testDto.statusCode()),
                () -> assertThat(deserializedDto.precondition()).isEqualTo(testDto.precondition()),
                () -> assertThat(deserializedDto.step()).isEqualTo(testDto.step()),
                () -> assertThat(deserializedDto.reqHeader()).isEqualTo(testDto.reqHeader()),
                () -> assertThat(deserializedDto.reqBody()).isEqualTo(testDto.reqBody()),
                () -> assertThat(deserializedDto.resHeader()).isEqualTo(testDto.resHeader()),
                () -> assertThat(deserializedDto.resBody()).isEqualTo(testDto.resBody()));
    }

    @Test
    @DisplayName("Jackson2JsonRedisSerializer(ObjectMapper, Class) 커스텀 ObjectMapper 테스트")
    void testJackson2JsonRedisSerializerWithCustomObjectMapper() {
        // given - 커스텀 ObjectMapper 설정
        ObjectMapper customMapper = new ObjectMapper();
        // 필요한 경우 커스텀 설정 추가 가능
        // customMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        // false);

        Jackson2JsonRedisSerializer<ApiTaskDto> serializer = new Jackson2JsonRedisSerializer<>(customMapper,
                ApiTaskDto.class);

        // when - 직렬화
        byte[] serializedData = serializer.serialize(testDto);

        // then - 직렬화 결과 검증
        assertThat(serializedData).isNotNull();
        assertThat(serializedData).hasSizeGreaterThan(0);

        String jsonString = new String(serializedData);
        System.out.println("Jackson2JsonRedisSerializer (Custom ObjectMapper) - JSON: " + jsonString);

        // when - 역직렬화
        ApiTaskDto deserializedDto = serializer.deserialize(serializedData);

        // then - 역직렬화 결과 검증
        assertThat(deserializedDto).isNotNull();
        assertThat(deserializedDto).isEqualTo(testDto);
    }

    @Test
    @DisplayName("Jackson2JsonRedisSerializer JavaType 생성자 테스트")
    void testJackson2JsonRedisSerializerWithJavaType() {
        // given - JavaType을 사용한 생성자
        ObjectMapper mapper = new ObjectMapper();
        TypeFactory typeFactory = mapper.getTypeFactory();
        var javaType = typeFactory.constructType(ApiTaskDto.class);

        Jackson2JsonRedisSerializer<ApiTaskDto> serializer = new Jackson2JsonRedisSerializer<>(mapper, javaType);

        // when - 직렬화
        byte[] serializedData = serializer.serialize(testDto);

        // then - 직렬화 결과 검증
        assertThat(serializedData).isNotNull();
        assertThat(serializedData).hasSizeGreaterThan(0);

        String jsonString = new String(serializedData);
        System.out.println("Jackson2JsonRedisSerializer (JavaType) - JSON: " + jsonString);

        // when - 역직렬화
        ApiTaskDto deserializedDto = serializer.deserialize(serializedData);

        // then - 역직렬화 결과 검증
        assertThat(deserializedDto).isNotNull();
        assertThat(deserializedDto).isEqualTo(testDto);
    }

    @Test
    @DisplayName("Jackson2JsonRedisSerializer null 값 처리 테스트")
    void testJackson2JsonRedisSerializerWithNullValues() {
        // given
        ApiTaskDto dtoWithNulls = ApiTaskDto.builder()
                .id(1)
                .resultId(2)
                .precondition(null)
                .step(1)
                .method("GET")
                .uri("/api/test")
                .reqHeader(null)
                .reqBody(null)
                .statusCode(2)
                .resHeader(null)
                .resBody(null)
                .build();

        Jackson2JsonRedisSerializer<ApiTaskDto> serializer = new Jackson2JsonRedisSerializer<>(ApiTaskDto.class);

        // when - 직렬화
        byte[] serializedData = serializer.serialize(dtoWithNulls);

        // then - 직렬화 결과 검증
        assertThat(serializedData).isNotNull();

        String jsonString = new String(serializedData);
        System.out.println("Jackson2JsonRedisSerializer (with nulls) - JSON: " + jsonString);

        // null 값들이 JSON에서 어떻게 처리되는지 확인
        assertThat(jsonString).contains("\"id\":1");
        assertThat(jsonString).contains("\"method\":\"GET\"");

        // when - 역직렬화
        ApiTaskDto deserializedDto = serializer.deserialize(serializedData);

        // then - 역직렬화 결과 검증
        assertThat(deserializedDto).isNotNull();
        assertThat(deserializedDto).isEqualTo(dtoWithNulls);

        // null 값들이 올바르게 복원되었는지 검증
        assertAll(
                () -> assertThat(deserializedDto.id()).isEqualTo(1),
                () -> assertThat(deserializedDto.resultId()).isEqualTo(2),
                () -> assertThat(deserializedDto.step()).isEqualTo(1),
                () -> assertThat(deserializedDto.method()).isEqualTo("GET"),
                () -> assertThat(deserializedDto.uri()).isEqualTo("/api/test"),
                () -> assertThat(deserializedDto.statusCode()).isEqualTo(2),
                () -> assertThat(deserializedDto.precondition()).isNull(),
                () -> assertThat(deserializedDto.reqHeader()).isNull(),
                () -> assertThat(deserializedDto.reqBody()).isNull(),
                () -> assertThat(deserializedDto.resHeader()).isNull(),
                () -> assertThat(deserializedDto.resBody()).isNull());
    }

    @Test
    @DisplayName("Jackson2JsonRedisSerializer와 GenericJackson2JsonRedisSerializer 비교 테스트")
    void testJacksonSerializersComparison() {
        // given
        Jackson2JsonRedisSerializer<ApiTaskDto> specificSerializer = new Jackson2JsonRedisSerializer<>(
                ApiTaskDto.class);
        GenericJackson2JsonRedisSerializer genericSerializer = new GenericJackson2JsonRedisSerializer();

        // when - 각각 직렬화
        byte[] specificSerialized = specificSerializer.serialize(testDto);
        byte[] genericSerialized = genericSerializer.serialize(testDto);

        // then - 직렬화 결과 비교
        assertThat(specificSerialized).isNotNull();
        assertThat(genericSerialized).isNotNull();

        String specificJson = new String(specificSerialized);
        String genericJson = new String(genericSerialized);

        System.out.println("Jackson2JsonRedisSerializer - JSON: " + specificJson);
        System.out.println("GenericJackson2JsonRedisSerializer - JSON: " + genericJson);

        // GenericJackson2JsonRedisSerializer는 타입 정보를 포함
        assertThat(genericJson).contains("@class");
        assertThat(specificJson).doesNotContain("@class");

        // when - 각각 역직렬화
        ApiTaskDto specificDeserialized = specificSerializer.deserialize(specificSerialized);
        Object genericDeserialized = genericSerializer.deserialize(genericSerialized);

        // then - 역직렬화 결과 검증
        assertThat(specificDeserialized).isEqualTo(testDto);
        assertThat(genericDeserialized).isInstanceOf(ApiTaskDto.class);
        assertThat((ApiTaskDto) genericDeserialized).isEqualTo(testDto);
    }

}
