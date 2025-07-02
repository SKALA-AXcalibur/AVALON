package com.sk.skala.axcalibur.apitest.feature.util;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ApiTaskDtoConverter 테스트")
public class ApiTaskDtoConverterTest {

    @Nested
    @DisplayName("toMap 메서드 테스트")
    class ToMapTests {

        @Test
        @DisplayName("정상적인 ApiTaskDto를 Map으로 변환")
        void toMap_ValidApiTaskDto_Success() {
            // given
            MultiValueMap<String, String> reqHeader = new LinkedMultiValueMap<>();
            reqHeader.add("Authorization", "Bearer token");
            reqHeader.add("Content-Type", "application/json");

            Map<String, Object> reqBody = new HashMap<>();
            reqBody.put("userId", 123);
            reqBody.put("name", "testUser");

            MultiValueMap<String, String> reqQuery = new LinkedMultiValueMap<>();
            reqQuery.add("page", "1");
            reqQuery.add("size", "10");

            Map<String, String> reqPath = new HashMap<>();
            reqPath.put("id", "100");

            MultiValueMap<String, String> resHeader = new LinkedMultiValueMap<>();
            resHeader.add("Content-Type", "application/json");

            Map<String, Object> resBody = new HashMap<>();
            resBody.put("status", "success");

            ApiTaskDto dto = ApiTaskDto.builder()
                    .id(1)
                    .testcaseId(2)
                    .resultId(3)
                    .precondition("step 1:header|Auth -> body|token")
                    .step(1)
                    .attempt(1)
                    .method("POST")
                    .uri("/api/test")
                    .statusCode(200)
                    .reqHeader(reqHeader)
                    .reqBody(reqBody)
                    .reqQuery(reqQuery)
                    .reqPath(reqPath)
                    .resHeader(resHeader)
                    .resBody(resBody)
                    .build();

            // when
            Map<String, String> result = ApiTaskDtoConverter.toMap(dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.get("id")).isEqualTo("1");
            assertThat(result.get("testcaseId")).isEqualTo("2");
            assertThat(result.get("resultId")).isEqualTo("3");
            assertThat(result.get("precondition")).isEqualTo("step 1:header|Auth -> body|token");
            assertThat(result.get("step")).isEqualTo("1");
            assertThat(result.get("attempt")).isEqualTo("1");
            assertThat(result.get("method")).isEqualTo("POST");
            assertThat(result.get("uri")).isEqualTo("/api/test");
            assertThat(result.get("statusCode")).isEqualTo("200");
            assertThat(result.get("reqHeader")).contains("Authorization");
            assertThat(result.get("reqBody")).contains("userId");
            assertThat(result.get("reqQuery")).contains("page");
            assertThat(result.get("reqPath")).contains("id");
            assertThat(result.get("resHeader")).contains("Content-Type");
            assertThat(result.get("resBody")).contains("status");
        }

        @Test
        @DisplayName("precondition이 null인 경우")
        void toMap_NullPrecondition_Success() {
            // given
            ApiTaskDto dto = ApiTaskDto.builder()
                    .id(1)
                    .testcaseId(2)
                    .resultId(3)
                    .precondition(null)
                    .step(1)
                    .attempt(1)
                    .method("GET")
                    .uri("/api/test")
                    .statusCode(200)
                    .reqHeader(new LinkedMultiValueMap<>())
                    .reqBody(new HashMap<>())
                    .reqQuery(new LinkedMultiValueMap<>())
                    .reqPath(new HashMap<>())
                    .resHeader(new LinkedMultiValueMap<>())
                    .resBody(new HashMap<>())
                    .build();

            // when
            Map<String, String> result = ApiTaskDtoConverter.toMap(dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result).doesNotContainKey("precondition");
        }

        @Test
        @DisplayName("빈 컬렉션들을 포함한 ApiTaskDto 변환")
        void toMap_EmptyCollections_Success() {
            // given
            ApiTaskDto dto = ApiTaskDto.builder()
                    .id(1)
                    .testcaseId(2)
                    .resultId(3)
                    .step(1)
                    .attempt(1)
                    .method("GET")
                    .uri("/api/test")
                    .statusCode(200)
                    .reqHeader(new LinkedMultiValueMap<>())
                    .reqBody(new HashMap<>())
                    .reqQuery(new LinkedMultiValueMap<>())
                    .reqPath(new HashMap<>())
                    .resHeader(new LinkedMultiValueMap<>())
                    .resBody(new HashMap<>())
                    .build();

            // when
            Map<String, String> result = ApiTaskDtoConverter.toMap(dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.get("reqHeader")).isEqualTo("{}");
            assertThat(result.get("reqBody")).isEqualTo("{}");
            assertThat(result.get("reqQuery")).isEqualTo("{}");
            assertThat(result.get("reqPath")).isEqualTo("{}");
            assertThat(result.get("resHeader")).isEqualTo("{}");
            assertThat(result.get("resBody")).isEqualTo("{}");
        }

        @Test
        @DisplayName("복잡한 reqBody 객체 구조 테스트")
        void toMap_ComplexReqBodyObject_Success() {
            // given
            Map<String, Object> reqBody = new HashMap<>();

            // 중첩 객체
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("name", "홍길동");
            userInfo.put("age", 30);
            userInfo.put("email", "hong@example.com");

            // 배열 데이터
            List<String> hobbies = Arrays.asList("독서", "영화감상", "여행");

            // 중첩된 배열 객체
            List<Map<String, Object>> addresses = new ArrayList<>();
            Map<String, Object> homeAddress = new HashMap<>();
            homeAddress.put("type", "home");
            homeAddress.put("street", "강남대로 123");
            homeAddress.put("city", "서울");
            homeAddress.put("zipCode", "06123");

            Map<String, Object> workAddress = new HashMap<>();
            workAddress.put("type", "work");
            workAddress.put("street", "테헤란로 456");
            workAddress.put("city", "서울");
            workAddress.put("zipCode", "06789");

            addresses.add(homeAddress);
            addresses.add(workAddress);

            // 복잡한 구조 조합
            reqBody.put("userId", 12345);
            reqBody.put("userInfo", userInfo);
            reqBody.put("hobbies", hobbies);
            reqBody.put("addresses", addresses);
            reqBody.put("isActive", true);
            reqBody.put("loginCount", 150);
            reqBody.put("lastLoginTime", "2024-01-15T10:30:00Z");

            ApiTaskDto dto = ApiTaskDto.builder()
                    .id(1)
                    .testcaseId(2)
                    .resultId(3)
                    .step(1)
                    .attempt(1)
                    .method("POST")
                    .uri("/api/user")
                    .statusCode(200)
                    .reqHeader(new LinkedMultiValueMap<>())
                    .reqBody(reqBody)
                    .reqQuery(new LinkedMultiValueMap<>())
                    .reqPath(new HashMap<>())
                    .resHeader(new LinkedMultiValueMap<>())
                    .resBody(new HashMap<>())
                    .build();

            // when
            Map<String, String> result = ApiTaskDtoConverter.toMap(dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.get("reqBody")).isNotNull();

            // JSON 문자열에 예상되는 키들이 포함되어 있는지 확인
            String reqBodyJson = result.get("reqBody");
            assertThat(reqBodyJson).contains("userId");
            assertThat(reqBodyJson).contains("userInfo");
            assertThat(reqBodyJson).contains("hobbies");
            assertThat(reqBodyJson).contains("addresses");
            assertThat(reqBodyJson).contains("홍길동");
            assertThat(reqBodyJson).contains("독서");
            assertThat(reqBodyJson).contains("강남대로 123");
        }

        @Test
        @DisplayName("복잡한 resBody 객체 구조 테스트")
        void toMap_ComplexResBodyObject_Success() {
            // given
            Map<String, Object> resBody = new HashMap<>();

            // API 응답 메타데이터
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("version", "1.0");
            metadata.put("timestamp", "2024-01-15T10:30:00Z");
            metadata.put("requestId", "req-12345");

            // 페이징 정보
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("page", 1);
            pagination.put("size", 10);
            pagination.put("totalElements", 150);
            pagination.put("totalPages", 15);

            // 실제 데이터 배열
            List<Map<String, Object>> data = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", i);
                item.put("name", "Item " + i);
                item.put("price", 10000 + (i * 1000));

                // 중첩 객체
                Map<String, Object> category = new HashMap<>();
                category.put("id", 100 + i);
                category.put("name", "Category " + i);
                category.put("description", "설명 " + i);
                item.put("category", category);

                data.add(item);
            }

            resBody.put("success", true);
            resBody.put("message", "조회 성공");
            resBody.put("metadata", metadata);
            resBody.put("pagination", pagination);
            resBody.put("data", data);

            ApiTaskDto dto = ApiTaskDto.builder()
                    .id(1)
                    .testcaseId(2)
                    .resultId(3)
                    .step(1)
                    .attempt(1)
                    .method("GET")
                    .uri("/api/items")
                    .statusCode(200)
                    .reqHeader(new LinkedMultiValueMap<>())
                    .reqBody(new HashMap<>())
                    .reqQuery(new LinkedMultiValueMap<>())
                    .reqPath(new HashMap<>())
                    .resHeader(new LinkedMultiValueMap<>())
                    .resBody(resBody)
                    .build();

            // when
            Map<String, String> result = ApiTaskDtoConverter.toMap(dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.get("resBody")).isNotNull();

            String resBodyJson = result.get("resBody");
            assertThat(resBodyJson).contains("success");
            assertThat(resBodyJson).contains("metadata");
            assertThat(resBodyJson).contains("pagination");
            assertThat(resBodyJson).contains("totalElements");
            assertThat(resBodyJson).contains("Category 1");
            assertThat(resBodyJson).contains("조회 성공");
        }
    }

    @Nested
    @DisplayName("fromMap 메서드 테스트")
    class FromMapTests {

        @Test
        @DisplayName("정상적인 Map을 ApiTaskDto로 변환")
        void fromMap_ValidMap_Success() {
            // given
            Map<String, String> map = new HashMap<>();
            map.put("id", "1");
            map.put("testcaseId", "2");
            map.put("resultId", "3");
            map.put("precondition", "step 1:header|Auth -> body|token");
            map.put("step", "1");
            map.put("attempt", "1");
            map.put("method", "POST");
            map.put("uri", "/api/test");
            map.put("statusCode", "200");
            map.put("reqHeader", "{\"Authorization\":[\"Bearer token\"],\"Content-Type\":[\"application/json\"]}");
            map.put("reqBody", "{\"userId\":123,\"name\":\"testUser\"}");
            map.put("reqQuery", "{\"page\":[\"1\"],\"size\":[\"10\"]}");
            map.put("reqPath", "{\"id\":\"100\"}");
            map.put("resHeader", "{\"Content-Type\":[\"application/json\"]}");
            map.put("resBody", "{\"status\":\"success\"}");

            // when
            ApiTaskDto result = ApiTaskDtoConverter.fromMap(map);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1);
            assertThat(result.testcaseId()).isEqualTo(2);
            assertThat(result.resultId()).isEqualTo(3);
            assertThat(result.precondition()).isEqualTo("step 1:header|Auth -> body|token");
            assertThat(result.step()).isEqualTo(1);
            assertThat(result.attempt()).isEqualTo(1);
            assertThat(result.method()).isEqualTo("POST");
            assertThat(result.uri()).isEqualTo("/api/test");
            assertThat(result.statusCode()).isEqualTo(200);
            assertThat(result.reqHeader()).isNotNull();
            assertThat(result.reqBody()).isNotNull();
            assertThat(result.reqQuery()).isNotNull();
            assertThat(result.reqPath()).isNotNull();
            assertThat(result.resHeader()).isNotNull();
            assertThat(result.resBody()).isNotNull();
        }

        @Test
        @DisplayName("null 값들을 포함한 Map 변환")
        void fromMap_WithNullValues_Success() {
            // given
            Map<String, String> map = new HashMap<>();
            map.put("id", "1");
            map.put("testcaseId", "2");
            map.put("resultId", "3");
            map.put("step", "1");
            map.put("attempt", "1");
            map.put("method", "GET");
            map.put("uri", "/api/test");
            map.put("statusCode", "200");
            // precondition과 일부 필드는 null로 설정

            // when
            ApiTaskDto result = ApiTaskDtoConverter.fromMap(map);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1);
            assertThat(result.precondition()).isNull();
            // ApiTaskDto 생성자에서 null을 빈 컬렉션으로 초기화
            assertThat(result.reqHeader()).isNotNull().isEmpty();
            assertThat(result.reqBody()).isNotNull().isEmpty();
            assertThat(result.resHeader()).isNotNull().isEmpty();
            assertThat(result.resBody()).isNotNull().isEmpty();
            // reqPath와 reqQuery도 생성자에서 빈 컬렉션으로 초기화
            assertThat(result.reqPath()).isNotNull().isEmpty();
            assertThat(result.reqQuery()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("따옴표로 둘러싸인 값들 처리")
        void fromMap_QuotedValues_Success() {
            // given
            Map<String, String> map = new HashMap<>();
            map.put("id", "\"1\"");
            map.put("testcaseId", "\"2\"");
            map.put("method", "\"POST\"");
            map.put("uri", "\"/api/test\"");
            map.put("step", "1");
            map.put("attempt", "1");
            map.put("resultId", "3");
            map.put("statusCode", "200");

            // when
            ApiTaskDto result = ApiTaskDtoConverter.fromMap(map);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1);
            assertThat(result.testcaseId()).isEqualTo(2);
            assertThat(result.method()).isEqualTo("POST");
            assertThat(result.uri()).isEqualTo("/api/test");
        }

        @Test
        @DisplayName("빈 문자열 값들 처리")
        void fromMap_EmptyStringValues_Success() {
            // given
            Map<String, String> map = new HashMap<>();
            map.put("id", "1");
            map.put("testcaseId", "2");
            map.put("resultId", "3");
            map.put("step", "1");
            map.put("attempt", "1");
            map.put("method", "GET");
            map.put("uri", "/api/test");
            map.put("statusCode", "200");
            map.put("precondition", "");
            map.put("reqHeader", "");
            map.put("reqBody", "");

            // when
            ApiTaskDto result = ApiTaskDtoConverter.fromMap(map);

            // then
            assertThat(result).isNotNull();
            assertThat(result.precondition()).isEmpty();
            // 빈 문자열은 deserialize에서 null로 반환되지만, ApiTaskDto 생성자에서 빈 컬렉션으로 초기화
            assertThat(result.reqHeader()).isNotNull().isEmpty();
            assertThat(result.reqBody()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("잘못된 숫자 형식 처리")
        void fromMap_InvalidNumberFormat_ThrowsException() {
            // given
            Map<String, String> map = new HashMap<>();
            map.put("id", "invalid");
            map.put("testcaseId", "2");
            map.put("resultId", "3");
            map.put("step", "1");
            map.put("attempt", "1");
            map.put("method", "GET");
            map.put("uri", "/api/test");
            map.put("statusCode", "200");

            // when & then
            assertThatThrownBy(() -> ApiTaskDtoConverter.fromMap(map))
                    .isInstanceOf(NumberFormatException.class);
        }

        @Test
        @DisplayName("복잡한 JSON 구조의 Map을 ApiTaskDto로 변환")
        void fromMap_ComplexJsonStructure_Success() {
            // given
            Map<String, String> map = new HashMap<>();
            map.put("id", "1");
            map.put("testcaseId", "2");
            map.put("resultId", "3");
            map.put("step", "1");
            map.put("attempt", "1");
            map.put("method", "POST");
            map.put("uri", "/api/user");
            map.put("statusCode", "200");

            // 복잡한 reqBody JSON 구조
            String complexReqBodyJson = """
                    {
                        "userId": 12345,
                        "userInfo": {
                            "name": "홍길동",
                            "age": 30,
                            "email": "hong@example.com"
                        },
                        "hobbies": ["독서", "영화감상", "여행"],
                        "addresses": [
                            {
                                "type": "home",
                                "street": "강남대로 123",
                                "city": "서울",
                                "zipCode": "06123"
                            },
                            {
                                "type": "work",
                                "street": "테헤란로 456",
                                "city": "서울",
                                "zipCode": "06789"
                            }
                        ],
                        "isActive": true,
                        "loginCount": 150,
                        "lastLoginTime": "2024-01-15T10:30:00Z"
                    }
                    """;

            // 복잡한 resBody JSON 구조
            String complexResBodyJson = """
                    {
                        "success": true,
                        "message": "조회 성공",
                        "metadata": {
                            "version": "1.0",
                            "timestamp": "2024-01-15T10:30:00Z",
                            "requestId": "req-12345"
                        },
                        "pagination": {
                            "page": 1,
                            "size": 10,
                            "totalElements": 150,
                            "totalPages": 15
                        },
                        "data": [
                            {
                                "id": 1,
                                "name": "Item 1",
                                "price": 11000,
                                "category": {
                                    "id": 101,
                                    "name": "Category 1",
                                    "description": "설명 1"
                                }
                            },
                            {
                                "id": 2,
                                "name": "Item 2",
                                "price": 12000,
                                "category": {
                                    "id": 102,
                                    "name": "Category 2",
                                    "description": "설명 2"
                                }
                            }
                        ]
                    }
                    """;

            map.put("reqBody", complexReqBodyJson);
            map.put("resBody", complexResBodyJson);

            // when
            ApiTaskDto result = ApiTaskDtoConverter.fromMap(map);

            // then
            assertThat(result).isNotNull();
            assertThat(result.reqBody()).isNotNull();
            assertThat(result.resBody()).isNotNull();

            // reqBody 검증
            Map<String, Object> reqBodyMap = result.reqBody();
            assertThat(reqBodyMap.get("userId")).isEqualTo(12345);
            assertThat(reqBodyMap.get("isActive")).isEqualTo(true);
            assertThat(reqBodyMap.get("loginCount")).isEqualTo(150);

            // 중첩 객체 검증
            @SuppressWarnings("unchecked")
            Map<String, Object> userInfo = (Map<String, Object>) reqBodyMap.get("userInfo");
            assertThat(userInfo.get("name")).isEqualTo("홍길동");
            assertThat(userInfo.get("age")).isEqualTo(30);

            // 배열 검증
            @SuppressWarnings("unchecked")
            List<String> hobbies = (List<String>) reqBodyMap.get("hobbies");
            assertThat(hobbies).containsExactly("독서", "영화감상", "여행");

            // 중첩 배열 객체 검증
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> addresses = (List<Map<String, Object>>) reqBodyMap.get("addresses");
            assertThat(addresses).hasSize(2);
            assertThat(addresses.get(0).get("type")).isEqualTo("home");
            assertThat(addresses.get(1).get("type")).isEqualTo("work");

            // resBody 검증
            Map<String, Object> resBodyMap = result.resBody();
            assertThat(resBodyMap.get("success")).isEqualTo(true);
            assertThat(resBodyMap.get("message")).isEqualTo("조회 성공");

            @SuppressWarnings("unchecked")
            Map<String, Object> metadata = (Map<String, Object>) resBodyMap.get("metadata");
            assertThat(metadata.get("version")).isEqualTo("1.0");
            assertThat(metadata.get("requestId")).isEqualTo("req-12345");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> data = (List<Map<String, Object>>) resBodyMap.get("data");
            assertThat(data).hasSize(2);
            assertThat(data.get(0).get("name")).isEqualTo("Item 1");

            @SuppressWarnings("unchecked")
            Map<String, Object> category = (Map<String, Object>) data.get(0).get("category");
            assertThat(category.get("name")).isEqualTo("Category 1");
        }

        // ...existing code...
    }

    @Nested
    @DisplayName("toMap과 fromMap 라운드트립 테스트")
    class RoundTripTests {

        @Test
        @DisplayName("toMap -> fromMap 라운드트립 테스트")
        void roundTrip_ToMapFromMap_Success() {
            // given
            MultiValueMap<String, String> reqHeader = new LinkedMultiValueMap<>();
            reqHeader.add("Authorization", "Bearer token");
            reqHeader.add("X-Custom", "value1");
            reqHeader.add("X-Custom", "value2"); // 중복 키

            Map<String, Object> reqBody = new HashMap<>();
            reqBody.put("userId", 123);
            reqBody.put("name", "testUser");
            reqBody.put("isActive", true);

            MultiValueMap<String, String> reqQuery = new LinkedMultiValueMap<>();
            reqQuery.add("filter", "name");
            reqQuery.add("filter", "age"); // 중복 키
            reqQuery.add("page", "1");

            Map<String, String> reqPath = new HashMap<>();
            reqPath.put("id", "100");
            reqPath.put("version", "v1");

            MultiValueMap<String, String> resHeader = new LinkedMultiValueMap<>();
            resHeader.add("Content-Type", "application/json");

            Map<String, Object> resBody = new HashMap<>();
            resBody.put("status", "success");
            resBody.put("count", 42);

            ApiTaskDto originalDto = ApiTaskDto.builder()
                    .id(1)
                    .testcaseId(2)
                    .resultId(3)
                    .precondition("step 1:header|Auth -> body|token")
                    .step(1)
                    .attempt(1)
                    .method("POST")
                    .uri("/api/test/{id}")
                    .statusCode(200)
                    .reqHeader(reqHeader)
                    .reqBody(reqBody)
                    .reqQuery(reqQuery)
                    .reqPath(reqPath)
                    .resHeader(resHeader)
                    .resBody(resBody)
                    .build();

            // when
            Map<String, String> map = ApiTaskDtoConverter.toMap(originalDto);
            ApiTaskDto convertedDto = ApiTaskDtoConverter.fromMap(map);

            // then
            assertThat(convertedDto.id()).isEqualTo(originalDto.id());
            assertThat(convertedDto.testcaseId()).isEqualTo(originalDto.testcaseId());
            assertThat(convertedDto.resultId()).isEqualTo(originalDto.resultId());
            assertThat(convertedDto.precondition()).isEqualTo(originalDto.precondition());
            assertThat(convertedDto.step()).isEqualTo(originalDto.step());
            assertThat(convertedDto.attempt()).isEqualTo(originalDto.attempt());
            assertThat(convertedDto.method()).isEqualTo(originalDto.method());
            assertThat(convertedDto.uri()).isEqualTo(originalDto.uri());
            assertThat(convertedDto.statusCode()).isEqualTo(originalDto.statusCode());

            // MultiValueMap은 정확한 비교가 어려우므로 키와 일부 값만 확인
            assertThat(convertedDto.reqHeader()).containsKey("Authorization");
            assertThat(convertedDto.reqHeader()).containsKey("X-Custom");
            assertThat(convertedDto.reqQuery()).containsKey("filter");
            assertThat(convertedDto.reqQuery()).containsKey("page");

            // Map은 정확한 비교 가능
            assertThat(convertedDto.reqPath()).containsEntry("id", "100");
            assertThat(convertedDto.reqPath()).containsEntry("version", "v1");
        }

        @Test
        @DisplayName("최소한의 데이터로 라운드트립 테스트")
        void roundTrip_MinimalData_Success() {
            // given
            ApiTaskDto originalDto = ApiTaskDto.builder()
                    .id(1)
                    .testcaseId(2)
                    .resultId(3)
                    .step(1)
                    .attempt(1)
                    .method("GET")
                    .uri("/api/test")
                    .statusCode(200)
                    .reqHeader(new LinkedMultiValueMap<>())
                    .reqBody(new HashMap<>())
                    .reqQuery(new LinkedMultiValueMap<>())
                    .reqPath(new HashMap<>())
                    .resHeader(new LinkedMultiValueMap<>())
                    .resBody(new HashMap<>())
                    .build();

            // when
            Map<String, String> map = ApiTaskDtoConverter.toMap(originalDto);
            ApiTaskDto convertedDto = ApiTaskDtoConverter.fromMap(map);

            // then
            assertThat(convertedDto.id()).isEqualTo(originalDto.id());
            assertThat(convertedDto.testcaseId()).isEqualTo(originalDto.testcaseId());
            assertThat(convertedDto.resultId()).isEqualTo(originalDto.resultId());
            assertThat(convertedDto.step()).isEqualTo(originalDto.step());
            assertThat(convertedDto.attempt()).isEqualTo(originalDto.attempt());
            assertThat(convertedDto.method()).isEqualTo(originalDto.method());
            assertThat(convertedDto.uri()).isEqualTo(originalDto.uri());
            assertThat(convertedDto.statusCode()).isEqualTo(originalDto.statusCode());
            assertThat(convertedDto.precondition()).isNull();
        }

        @Test
        @DisplayName("복잡한 객체 구조 라운드트립 테스트")
        void roundTrip_ComplexObjectStructure_Success() {
            // given
            Map<String, Object> complexReqBody = new HashMap<>();

            // 다양한 데이터 타입들
            complexReqBody.put("stringValue", "문자열 값");
            complexReqBody.put("intValue", 123);
            complexReqBody.put("doubleValue", 123.45);
            complexReqBody.put("booleanValue", true);
            complexReqBody.put("nullValue", null);

            // 배열들
            complexReqBody.put("stringArray", Arrays.asList("값1", "값2", "값3"));
            complexReqBody.put("numberArray", Arrays.asList(1, 2, 3, 4, 5));
            complexReqBody.put("mixedArray", Arrays.asList("문자", 123, true, null));

            // 중첩 객체
            Map<String, Object> nestedObject = new HashMap<>();
            nestedObject.put("level1", "첫 번째 레벨");

            Map<String, Object> deeplyNested = new HashMap<>();
            deeplyNested.put("level2", "두 번째 레벨");
            deeplyNested.put("numbers", Arrays.asList(10, 20, 30));
            nestedObject.put("nested", deeplyNested);

            complexReqBody.put("nestedData", nestedObject);

            // 객체 배열
            List<Map<String, Object>> objectArray = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                Map<String, Object> obj = new HashMap<>();
                obj.put("id", i);
                obj.put("name", "객체 " + i);
                obj.put("tags", Arrays.asList("tag" + i + "a", "tag" + i + "b"));
                objectArray.add(obj);
            }
            complexReqBody.put("objectArray", objectArray);

            Map<String, Object> complexResBody = new HashMap<>();
            complexResBody.put("status", "success");
            complexResBody.put("code", 200);
            complexResBody.put("timestamp", "2024-01-15T10:30:00Z");

            // 복잡한 응답 데이터
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("processedCount", 100);
            responseData.put("errors", Arrays.asList());
            responseData.put("warnings", Arrays.asList("경고 메시지 1", "경고 메시지 2"));

            Map<String, Object> summary = new HashMap<>();
            summary.put("total", 100);
            summary.put("success", 98);
            summary.put("failed", 2);
            responseData.put("summary", summary);

            complexResBody.put("data", responseData);

            ApiTaskDto originalDto = ApiTaskDto.builder()
                    .id(999)
                    .testcaseId(888)
                    .resultId(777)
                    .precondition("step 1:header|X-Token -> body|authToken")
                    .step(5)
                    .attempt(3)
                    .method("PUT")
                    .uri("/api/complex/{id}")
                    .statusCode(200)
                    .reqHeader(new LinkedMultiValueMap<>())
                    .reqBody(complexReqBody)
                    .reqQuery(new LinkedMultiValueMap<>())
                    .reqPath(Map.of("id", "complex-123"))
                    .resHeader(new LinkedMultiValueMap<>())
                    .resBody(complexResBody)
                    .build();

            // when
            Map<String, String> map = ApiTaskDtoConverter.toMap(originalDto);
            ApiTaskDto convertedDto = ApiTaskDtoConverter.fromMap(map);

            // then
            assertThat(convertedDto.id()).isEqualTo(originalDto.id());
            assertThat(convertedDto.testcaseId()).isEqualTo(originalDto.testcaseId());
            assertThat(convertedDto.resultId()).isEqualTo(originalDto.resultId());
            assertThat(convertedDto.precondition()).isEqualTo(originalDto.precondition());
            assertThat(convertedDto.method()).isEqualTo(originalDto.method());
            assertThat(convertedDto.uri()).isEqualTo(originalDto.uri());

            // 복잡한 reqBody 검증
            Map<String, Object> convertedReqBody = convertedDto.reqBody();
            assertThat(convertedReqBody.get("stringValue")).isEqualTo("문자열 값");
            assertThat(convertedReqBody.get("intValue")).isEqualTo(123);
            assertThat(convertedReqBody.get("booleanValue")).isEqualTo(true);

            // 배열 검증
            @SuppressWarnings("unchecked")
            List<String> stringArray = (List<String>) convertedReqBody.get("stringArray");
            assertThat(stringArray).containsExactly("값1", "값2", "값3");

            // 중첩 객체 검증
            @SuppressWarnings("unchecked")
            Map<String, Object> convertedNested = (Map<String, Object>) convertedReqBody.get("nestedData");
            assertThat(convertedNested.get("level1")).isEqualTo("첫 번째 레벨");

            @SuppressWarnings("unchecked")
            Map<String, Object> convertedDeepNested = (Map<String, Object>) convertedNested.get("nested");
            assertThat(convertedDeepNested.get("level2")).isEqualTo("두 번째 레벨");

            // 객체 배열 검증
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> convertedObjectArray = (List<Map<String, Object>>) convertedReqBody
                    .get("objectArray");
            assertThat(convertedObjectArray).hasSize(3);
            assertThat(convertedObjectArray.get(0).get("name")).isEqualTo("객체 1");

            // 복잡한 resBody 검증
            Map<String, Object> convertedResBody = convertedDto.resBody();
            assertThat(convertedResBody.get("status")).isEqualTo("success");
            assertThat(convertedResBody.get("code")).isEqualTo(200);

            @SuppressWarnings("unchecked")
            Map<String, Object> convertedData = (Map<String, Object>) convertedResBody.get("data");
            assertThat(convertedData.get("processedCount")).isEqualTo(100);

            @SuppressWarnings("unchecked")
            Map<String, Object> convertedSummary = (Map<String, Object>) convertedData.get("summary");
            assertThat(convertedSummary.get("success")).isEqualTo(98);
            assertThat(convertedSummary.get("failed")).isEqualTo(2);
        }

        // ...existing code...
    }

    @Nested
    @DisplayName("예외 상황 테스트")
    class ExceptionTests {
        @Test
        @DisplayName("잘못된 JSON 형식의 reqHeader 처리")
        void fromMap_InvalidJsonReqHeader_ReturnsEmpty() {
            // given
            Map<String, String> map = new HashMap<>();
            map.put("id", "1");
            map.put("testcaseId", "2");
            map.put("resultId", "3");
            map.put("step", "1");
            map.put("attempt", "1");
            map.put("method", "GET");
            map.put("uri", "/api/test");
            map.put("statusCode", "200");
            map.put("reqHeader", "{invalid json}");

            // when
            ApiTaskDto result = ApiTaskDtoConverter.fromMap(map);

            // then
            assertThat(result).isNotNull();
            // 잘못된 JSON은 null로 반환되지만, ApiTaskDto 생성자에서 빈 컬렉션으로 초기화
            assertThat(result.reqHeader()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("잘못된 JSON 형식의 reqBody 처리")
        void fromMap_InvalidJsonReqBody_ReturnsEmpty() {
            // given
            Map<String, String> map = new HashMap<>();
            map.put("id", "1");
            map.put("testcaseId", "2");
            map.put("resultId", "3");
            map.put("step", "1");
            map.put("attempt", "1");
            map.put("method", "GET");
            map.put("uri", "/api/test");
            map.put("statusCode", "200");
            map.put("reqBody", "{invalid json}");

            // when
            ApiTaskDto result = ApiTaskDtoConverter.fromMap(map);

            // then
            assertThat(result).isNotNull();
            // 잘못된 JSON은 null로 반환되지만, ApiTaskDto 생성자에서 빈 컬렉션으로 초기화
            assertThat(result.reqBody()).isNotNull().isEmpty();
        }
    }
}
