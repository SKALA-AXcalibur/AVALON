package com.sk.skala.axcalibur.apitest.feature.service;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiRequestDataDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ParameterWithDataDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DisplayName("ApiTestService buildTaskData 메서드 테스트")
class ApiTestServiceBuildTaskDataTest {

    @Autowired
    private ApiTestService apiTestService;

    @Nested
    @DisplayName("기본 데이터 타입 처리 테스트")
    class BasicDataTypeTests {

        @Test
        @DisplayName("String 타입 요청 헤더 파라미터 처리")
        void shouldHandleStringRequestHeaderParameter() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(1)
                            .parameterName("Authorization")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("header")
                            .parentId(null)
                            .testcaseId(1)
                            .value("Bearer token123")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.reqHeader()).isNotEmpty();
            assertThat(result.reqHeader().getFirst("Authorization")).isEqualTo("Bearer token123");
            assertThat(result.reqQuery()).isEmpty();
            assertThat(result.reqBody()).isEmpty();
            assertThat(result.reqPath()).isEmpty();
        }

        @Test
        @DisplayName("Integer 타입 요청 쿼리 파라미터 처리")
        void shouldHandleIntegerRequestQueryParameter() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(2)
                            .parameterName("page")
                            .dataType("integer")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("query")
                            .parentId(null)
                            .testcaseId(1)
                            .value("5")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.reqQuery()).isNotEmpty();
            assertThat(result.reqQuery().getFirst("page")).isEqualTo("5");
        }

        @Test
        @DisplayName("Boolean 타입 요청 바디 파라미터 처리")
        void shouldHandleBooleanRequestBodyParameter() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(3)
                            .parameterName("isActive")
                            .dataType("boolean")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(null)
                            .testcaseId(1)
                            .value("true")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.reqBody()).isNotEmpty();
            assertThat(result.reqBody().get("isActive")).isEqualTo(true);
        }

        @Test
        @DisplayName("BigDecimal 타입 요청 바디 파라미터 처리")
        void shouldHandleBigDecimalRequestBodyParameter() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(4)
                            .parameterName("amount")
                            .dataType("bigdecimal")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(null)
                            .testcaseId(1)
                            .value("123.45")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.reqBody()).isNotEmpty();
            assertThat(result.reqBody().get("amount")).isEqualTo(new BigDecimal("123.45"));
        }

        @Test
        @DisplayName("경로 변수 파라미터 처리")
        void shouldHandlePathParameter() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(5)
                            .parameterName("userId")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("path")
                            .parentId(null)
                            .testcaseId(1)
                            .value("12345")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.reqPath()).isNotEmpty();
            assertThat(result.reqPath().get("userId")).isEqualTo("12345");
        }
    }

    @Nested
    @DisplayName("응답 파라미터 처리 테스트")
    class ResponseParameterTests {

        @Test
        @DisplayName("응답 헤더 파라미터 처리")
        void shouldHandleResponseHeaderParameter() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(6)
                            .parameterName("Content-Type")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("response")
                            .contextName("header")
                            .parentId(null)
                            .testcaseId(1)
                            .value("application/json")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.resHeader()).isNotEmpty();
            assertThat(result.resHeader().getFirst("Content-Type")).isEqualTo("application/json");
            assertThat(result.reqHeader()).isEmpty();
        }

        @Test
        @DisplayName("응답 바디 파라미터 처리")
        void shouldHandleResponseBodyParameter() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(7)
                            .parameterName("status")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("response")
                            .contextName("body")
                            .parentId(null)
                            .testcaseId(1)
                            .value("success")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.resBody()).isNotEmpty();
            assertThat(result.resBody().get("status")).isEqualTo("success");
        }
    }

    @Nested
    @DisplayName("배열 타입 처리 테스트")
    class ArrayTypeTests {

        @Test
        @DisplayName("문자열 배열 파라미터 처리 - 쉼표로 구분된 값")
        void shouldHandleStringArrayParameter() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(8)
                            .parameterName("tags")
                            .dataType("array")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(null)
                            .testcaseId(1)
                            .value("tag1,tag2,tag3")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.reqBody()).isNotEmpty();
            assertThat(result.reqBody().get("tags")).isInstanceOf(List.class);
            @SuppressWarnings("unchecked")
            List<String> tags = (List<String>) result.reqBody().get("tags");
            assertThat(tags).containsExactly("tag1", "tag2", "tag3");
        }

        @Test
        @DisplayName("자식 요소를 가진 배열 파라미터 처리")
        void shouldHandleArrayParameterWithChildren() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(9)
                            .parameterName("items")
                            .dataType("array")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(null)
                            .testcaseId(1)
                            .value(null)
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(10)
                            .parameterName("item1")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(9)
                            .testcaseId(1)
                            .value("first item")
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(11)
                            .parameterName("item2")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(9)
                            .testcaseId(1)
                            .value("second item")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.reqBody()).isNotEmpty();
            assertThat(result.reqBody().get("items")).isInstanceOf(List.class);
            @SuppressWarnings("unchecked")
            List<Object> items = (List<Object>) result.reqBody().get("items");
            assertThat(items).hasSize(2);
            assertThat(items).containsExactly("first item", "second item");
        }
    }

    @Nested
    @DisplayName("객체 타입 처리 테스트")
    class ObjectTypeTests {

        @Test
        @DisplayName("JSON 문자열로 된 객체 파라미터 처리")
        void shouldHandleObjectParameterFromJsonString() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(12)
                            .parameterName("user")
                            .dataType("object")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(null)
                            .testcaseId(1)
                            .value("{\"name\":\"John\",\"age\":30}")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.reqBody()).isNotEmpty();
            assertThat(result.reqBody().get("user")).isInstanceOf(Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) result.reqBody().get("user");
            assertThat(user.get("name")).isEqualTo("John");
            assertThat(user.get("age")).isEqualTo(30);
        }

        @Test
        @DisplayName("자식 요소를 가진 객체 파라미터 처리")
        void shouldHandleObjectParameterWithChildren() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(13)
                            .parameterName("address")
                            .dataType("object")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(null)
                            .testcaseId(1)
                            .value(null)
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(14)
                            .parameterName("street")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(13)
                            .testcaseId(1)
                            .value("123 Main St")
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(15)
                            .parameterName("city")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(13)
                            .testcaseId(1)
                            .value("Seoul")
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(16)
                            .parameterName("zipCode")
                            .dataType("integer")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(13)
                            .testcaseId(1)
                            .value("12345")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.reqBody()).isNotEmpty();
            assertThat(result.reqBody().get("address")).isInstanceOf(Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> address = (Map<String, Object>) result.reqBody().get("address");
            assertThat(address.get("street")).isEqualTo("123 Main St");
            assertThat(address.get("city")).isEqualTo("Seoul");
            assertThat(address.get("zipCode")).isEqualTo(12345);
        }
    }

    @Nested
    @DisplayName("중첩 구조 처리 테스트")
    class NestedStructureTests {

        @Test
        @DisplayName("객체 안의 배열 파라미터 처리")
        void shouldHandleArrayInsideObject() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(17)
                            .parameterName("profile")
                            .dataType("object")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(null)
                            .testcaseId(1)
                            .value(null)
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(18)
                            .parameterName("skills")
                            .dataType("array")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(17)
                            .testcaseId(1)
                            .value("Java,Spring,React")
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(19)
                            .parameterName("experience")
                            .dataType("integer")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(17)
                            .testcaseId(1)
                            .value("5")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.reqBody()).isNotEmpty();
            assertThat(result.reqBody().get("profile")).isInstanceOf(Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> profile = (Map<String, Object>) result.reqBody().get("profile");
            assertThat(profile.get("skills")).isInstanceOf(List.class);
            @SuppressWarnings("unchecked")
            List<String> skills = (List<String>) profile.get("skills");
            assertThat(skills).containsExactly("Java", "Spring", "React");
            assertThat(profile.get("experience")).isEqualTo(5);
        }

        @Test
        @DisplayName("배열 안의 객체 파라미터 처리")
        void shouldHandleObjectInsideArray() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(20)
                            .parameterName("users")
                            .dataType("array")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(null)
                            .testcaseId(1)
                            .value(null)
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(21)
                            .parameterName("user1")
                            .dataType("object")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(20)
                            .testcaseId(1)
                            .value(null)
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(22)
                            .parameterName("name")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(21)
                            .testcaseId(1)
                            .value("Alice")
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(23)
                            .parameterName("age")
                            .dataType("integer")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(21)
                            .testcaseId(1)
                            .value("25")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.reqBody()).isNotEmpty();
            assertThat(result.reqBody().get("users")).isInstanceOf(List.class);
            @SuppressWarnings("unchecked")
            List<Object> users = (List<Object>) result.reqBody().get("users");
            assertThat(users).hasSize(1);
            assertThat(users.get(0)).isInstanceOf(Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) users.get(0);
            assertThat(user.get("name")).isEqualTo("Alice");
            assertThat(user.get("age")).isEqualTo(25);
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    class ComplexScenarioTests {

        @Test
        @DisplayName("모든 컨텍스트를 포함한 복합 요청 처리")
        void shouldHandleComplexRequestWithAllContexts() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    // 요청 헤더
                    ParameterWithDataDto.builder()
                            .parameterId(24)
                            .parameterName("Authorization")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("header")
                            .parentId(null)
                            .testcaseId(1)
                            .value("Bearer token123")
                            .build(),
                    // 요청 쿼리
                    ParameterWithDataDto.builder()
                            .parameterId(25)
                            .parameterName("limit")
                            .dataType("integer")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("query")
                            .parentId(null)
                            .testcaseId(1)
                            .value("10")
                            .build(),
                    // 요청 경로
                    ParameterWithDataDto.builder()
                            .parameterId(26)
                            .parameterName("userId")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("path")
                            .parentId(null)
                            .testcaseId(1)
                            .value("user123")
                            .build(),
                    // 요청 바디 - 복합 객체
                    ParameterWithDataDto.builder()
                            .parameterId(27)
                            .parameterName("requestData")
                            .dataType("object")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(null)
                            .testcaseId(1)
                            .value(null)
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(28)
                            .parameterName("message")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(27)
                            .testcaseId(1)
                            .value("Hello World")
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(29)
                            .parameterName("priority")
                            .dataType("integer")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(27)
                            .testcaseId(1)
                            .value("1")
                            .build(),
                    // 응답 헤더
                    ParameterWithDataDto.builder()
                            .parameterId(30)
                            .parameterName("Content-Type")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("response")
                            .contextName("header")
                            .parentId(null)
                            .testcaseId(1)
                            .value("application/json")
                            .build(),
                    // 응답 바디
                    ParameterWithDataDto.builder()
                            .parameterId(31)
                            .parameterName("success")
                            .dataType("boolean")
                            .apiListId(1)
                            .categoryName("response")
                            .contextName("body")
                            .parentId(null)
                            .testcaseId(1)
                            .value("true")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();

            // 요청 헤더 검증
            assertThat(result.reqHeader()).isNotEmpty();
            assertThat(result.reqHeader().getFirst("Authorization")).isEqualTo("Bearer token123");

            // 요청 쿼리 검증
            assertThat(result.reqQuery()).isNotEmpty();
            assertThat(result.reqQuery().getFirst("limit")).isEqualTo("10");

            // 요청 경로 검증
            assertThat(result.reqPath()).isNotEmpty();
            assertThat(result.reqPath().get("userId")).isEqualTo("user123");

            // 요청 바디 검증
            assertThat(result.reqBody()).isNotEmpty();
            assertThat(result.reqBody().get("requestData")).isInstanceOf(Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> requestData = (Map<String, Object>) result.reqBody().get("requestData");
            assertThat(requestData.get("message")).isEqualTo("Hello World");
            assertThat(requestData.get("priority")).isEqualTo(1);

            // 응답 헤더 검증
            assertThat(result.resHeader()).isNotEmpty();
            assertThat(result.resHeader().getFirst("Content-Type")).isEqualTo("application/json");

            // 응답 바디 검증
            assertThat(result.resBody()).isNotEmpty();
            assertThat(result.resBody().get("success")).isEqualTo(true);
        }

        @Test
        @DisplayName("빈 파라미터 리스트 처리")
        void shouldHandleEmptyParameterList() {
            // Given
            List<ParameterWithDataDto> parameters = List.of();

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.reqHeader()).isEmpty();
            assertThat(result.reqQuery()).isEmpty();
            assertThat(result.reqBody()).isEmpty();
            assertThat(result.reqPath()).isEmpty();
            assertThat(result.resHeader()).isEmpty();
            assertThat(result.resBody()).isEmpty();
        }

        @Test
        @DisplayName("null 값을 가진 파라미터 처리")
        void shouldHandleParametersWithNullValues() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(32)
                            .parameterName("optionalField")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(null)
                            .testcaseId(1)
                            .value(null)
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.reqBody()).isEmpty(); // null 값은 포함되지 않음
        }
    }

    @Nested
    @DisplayName("convertValueByDataType 메서드 테스트")
    class ConvertValueByDataTypeTests {

        @Test
        @DisplayName("다양한 데이터 타입 변환 테스트")
        void shouldConvertVariousDataTypes() {
            // Integer 변환
            assertThat(apiTestService.convertValueByDataType("123", "integer")).isEqualTo(123);
            assertThat(apiTestService.convertValueByDataType("123", "int")).isEqualTo(123);

            // Long 변환
            assertThat(apiTestService.convertValueByDataType("123456789", "long")).isEqualTo(123456789L);

            // Double 변환
            assertThat(apiTestService.convertValueByDataType("123.45", "double")).isEqualTo(123.45);

            // Float 변환
            assertThat(apiTestService.convertValueByDataType("123.45", "float")).isEqualTo(123.45f);

            // Boolean 변환
            assertThat(apiTestService.convertValueByDataType("true", "boolean")).isEqualTo(true);
            assertThat(apiTestService.convertValueByDataType("false", "boolean")).isEqualTo(false);

            // BigDecimal 변환
            assertThat(apiTestService.convertValueByDataType("123.456", "bigdecimal"))
                    .isEqualTo(new BigDecimal("123.456"));

            // String 변환 (기본값)
            assertThat(apiTestService.convertValueByDataType("test", "string")).isEqualTo("test");
            assertThat(apiTestService.convertValueByDataType("test", "unknown")).isEqualTo("test");
        }

        @Test
        @DisplayName("잘못된 형식의 숫자 변환 시 문자열로 반환")
        void shouldReturnStringWhenInvalidNumberFormat() {
            // Given & When & Then
            assertThat(apiTestService.convertValueByDataType("invalid", "integer")).isEqualTo("invalid");
            assertThat(apiTestService.convertValueByDataType("not-a-number", "double"))
                    .isEqualTo("not-a-number");
            assertThat(apiTestService.convertValueByDataType("xyz", "bigdecimal")).isEqualTo("xyz");
        }

        @Test
        @DisplayName("null이나 빈 값 처리")
        void shouldHandleNullOrEmptyValues() {
            // Given & When & Then
            assertThat(apiTestService.convertValueByDataType(null, "integer")).isNull();
            assertThat(apiTestService.convertValueByDataType("", "integer")).isEqualTo("");
            assertThat(apiTestService.convertValueByDataType("   ", "integer")).isEqualTo("   ");
        }
    }

    @Nested
    @DisplayName("특수 컨텍스트 처리 테스트")
    class SpecialContextTests {

        @Test
        @DisplayName("세션/쿠키 파라미터를 헤더로 변환")
        void shouldConvertSessionCookieParametersToHeaders() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(33)
                            .parameterName("sessionId")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("session")
                            .parentId(null)
                            .testcaseId(1)
                            .value("abc123")
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(34)
                            .parameterName("token")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("request")
                            .contextName("cookie")
                            .parentId(null)
                            .testcaseId(1)
                            .value("xyz789")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.reqHeader()).isNotEmpty();
            List<String> cookies = result.reqHeader().get("Cookie");
            assertThat(cookies).hasSize(2);
            assertThat(cookies).contains("sessionId=abc123", "token=xyz789");
        }

        @Test
        @DisplayName("카테고리가 명시되지 않은 파라미터는 요청으로 처리")
        void shouldTreatUncategorizedParametersAsRequest() {
            // Given
            List<ParameterWithDataDto> parameters = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(35)
                            .parameterName("uncategorized")
                            .dataType("string")
                            .apiListId(1)
                            .categoryName("") // 빈 카테고리
                            .contextName("body")
                            .parentId(null)
                            .testcaseId(1)
                            .value("test value")
                            .build());

            // When
            ApiRequestDataDto result = apiTestService.buildTaskData(parameters);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.reqBody()).isNotEmpty();
            assertThat(result.reqBody().get("uncategorized")).isEqualTo("test value");
            assertThat(result.resBody()).isEmpty();
        }
    }
}