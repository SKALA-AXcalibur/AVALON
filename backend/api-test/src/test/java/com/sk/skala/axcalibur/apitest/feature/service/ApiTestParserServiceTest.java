package com.sk.skala.axcalibur.apitest.feature.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTestParserServiceBuildUriRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTestParserServiceParsePreconditionRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ApiTestParserServiceParsePreconditionResponseDto;
import com.sk.skala.axcalibur.apitest.feature.entity.ApiTestDetailRedisEntity;
import com.sk.skala.axcalibur.apitest.feature.repository.ApiTestDetailRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApiTestParserService 테스트")
public class ApiTestParserServiceTest {

    @Mock
    private ApiTestDetailRepository repo;

    @InjectMocks
    private ApiTestParserServiceImpl apiTestParserService;

    private ApiTestDetailRedisEntity mockEntity;
    private ApiTestParserServiceParsePreconditionRequestDto requestDto;

    @BeforeEach
    void setUp() {
        // Mock Entity 데이터 설정
        Map<String, String> path = new HashMap<>();
        path.put("userId", "12345");
        path.put("orderId", "67890");

        MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
        query.add("page", "1");
        query.add("size", "10");
        query.add("filter", "active");
        query.add("filter", "pending");

        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer token123");
        header.add("Content-Type", "application/json");
        header.add("X-Custom", "value1");
        header.add("X-Custom", "value2");

        Map<String, Object> body = new HashMap<>();
        body.put("token", "abc123");
        body.put("userId", 98765);
        body.put("status", "success");
        Map<String, Object> nestedData = new HashMap<>();
        nestedData.put("email", "test@example.com");
        nestedData.put("name", "Test User");
        body.put("userInfo", nestedData);

        mockEntity = ApiTestDetailRedisEntity.builder()
                .id("100-1-200")
                .resultId(1)
                .path(path)
                .query(query)
                .header(header)
                .body(body)
                .build();

        requestDto = ApiTestParserServiceParsePreconditionRequestDto.builder()
                .scenarioKey(100)
                .statusCode(200)
                .build();
    }

    @Nested
    @DisplayName("정상 케이스 테스트")
    class SuccessfulCases {

        @Test
        @DisplayName("Header에서 Body로 데이터 전송 - 단일 값")
        void parsePrecondition_HeaderToBody_SingleValue_Success() throws ParseException {
            // given
            String precondition = "step 1:header|Authorization -> body|authToken";
            requestDto = requestDto.toBuilder().precondition(precondition).build();
            when(repo.findById("100-1-200")).thenReturn(Optional.of(mockEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.body()).containsEntry("authToken", "Bearer token123");
            assertThat(response.path()).isEmpty();
            assertThat(response.query()).isEmpty();
            assertThat(response.header()).isEmpty();
        }

        @Test
        @DisplayName("Header에서 Body로 데이터 전송 - 다중 값")
        void parsePrecondition_HeaderToBody_MultipleValues_Success() throws ParseException {
            // given
            String precondition = "step 1:header|X-Custom -> body|customValues";
            requestDto = requestDto.toBuilder().precondition(precondition).build();
            when(repo.findById("100-1-200")).thenReturn(Optional.of(mockEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.body()).containsEntry("customValues", List.of("value1", "value2"));
        }

        @Test
        @DisplayName("Body에서 Path로 데이터 전송")
        void parsePrecondition_BodyToPath_Success() throws ParseException {
            // given
            String precondition = "step 1:body|userId -> path|id";
            requestDto = requestDto.toBuilder().precondition(precondition).build();
            when(repo.findById("100-1-200")).thenReturn(Optional.of(mockEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.path()).containsEntry("id", "98765");
            assertThat(response.body()).isEmpty();
            assertThat(response.query()).isEmpty();
            assertThat(response.header()).isEmpty();
        }

        @Test
        @DisplayName("Path에서 Query로 데이터 전송")
        void parsePrecondition_PathToQuery_Success() throws ParseException {
            // given
            String precondition = "step 1:path|userId -> query|user";
            requestDto = requestDto.toBuilder().precondition(precondition).build();
            when(repo.findById("100-1-200")).thenReturn(Optional.of(mockEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.query().get("user")).containsExactly("12345");
            assertThat(response.path()).isEmpty();
            assertThat(response.body()).isEmpty();
            assertThat(response.header()).isEmpty();
        }

        @Test
        @DisplayName("Query에서 Header로 데이터 전송")
        void parsePrecondition_QueryToHeader_Success() throws ParseException {
            // given
            String precondition = "step 1:query|filter -> header|X-Filter";
            requestDto = requestDto.toBuilder().precondition(precondition).build();
            when(repo.findById("100-1-200")).thenReturn(Optional.of(mockEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.header().get("X-Filter"))
                    .containsExactlyInAnyOrder("active", "pending");
            assertThat(response.path()).isEmpty();
            assertThat(response.body()).isEmpty();
            assertThat(response.query()).isEmpty();
        }

        @Test
        @DisplayName("복수의 사전 조건 처리")
        void parsePrecondition_MultiplePreconditions_Success() throws ParseException {
            // given
            String precondition = "step 1:body|token -> header|Authorization, step 1:path|userId -> query|user";
            requestDto = requestDto.toBuilder().precondition(precondition).build();
            when(repo.findById("100-1-200")).thenReturn(Optional.of(mockEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.header().get("Authorization")).containsExactly("abc123");
            assertThat(response.query().get("user")).containsExactly("12345");
            assertThat(response.path()).isEmpty();
            assertThat(response.body()).isEmpty();
        }

        @Test
        @DisplayName("다른 step의 데이터 참조")
        void parsePrecondition_DifferentStep_Success() throws ParseException {
            // given
            String precondition = "step 2:body|status -> path|statusCode";
            requestDto = requestDto.toBuilder().precondition(precondition).build();

            // step 2의 entity 준비
            when(repo.findById("100-2-200")).thenReturn(Optional.of(mockEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.path()).containsEntry("statusCode", "success");
        }

        @Test
        @DisplayName("Query에서 Body로 데이터 전송 - 단일 값")
        void parsePrecondition_QueryToBody_SingleValue_Success() throws ParseException {
            // given
            String precondition = "step 1:query|page -> body|currentPage";
            requestDto = requestDto.toBuilder().precondition(precondition).build();
            when(repo.findById("100-1-200")).thenReturn(Optional.of(mockEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.body()).containsEntry("currentPage", "1");
        }

        @Test
        @DisplayName("Query에서 Body로 데이터 전송 - 다중 값")
        void parsePrecondition_QueryToBody_MultipleValues_Success() throws ParseException {
            // given
            String precondition = "step 1:query|filter -> body|filters";
            requestDto = requestDto.toBuilder().precondition(precondition).build();
            when(repo.findById("100-1-200")).thenReturn(Optional.of(mockEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.body()).containsEntry("filters", List.of("active", "pending"));
        }
    }

    @Nested
    @DisplayName("에러 케이스 테스트")
    class ErrorCases {

        @Test
        @DisplayName("잘못된 step 번호 - 0")
        void parsePrecondition_InvalidStepNumber_Zero_ThrowsException() {
            // given
            String precondition = "step 0:header|test -> body|test";
            requestDto = requestDto.toBuilder().precondition(precondition).build();

            // when & then
            ParseException exception = assertThrows(ParseException.class,
                    () -> apiTestParserService.parsePrecondition(requestDto));
            assertThat(exception.getMessage()).contains("Invalid step: 0");
        }

        @Test
        @DisplayName("잘못된 step 번호 - 음수")
        void parsePrecondition_InvalidStepNumber_Negative_ThrowsException() {
            // given
            String precondition = "step -1:header|test -> body|test";
            requestDto = requestDto.toBuilder().precondition(precondition).build();

            // when & then
            ParseException exception = assertThrows(ParseException.class,
                    () -> apiTestParserService.parsePrecondition(requestDto));
            assertThat(exception.getMessage()).contains("Invalid step: -1");
        }

        @Test
        @DisplayName("잘못된 step 번호 - 문자열")
        void parsePrecondition_InvalidStepNumber_String_ThrowsException() {
            // given
            String precondition = "step abc:header|test -> body|test";
            requestDto = requestDto.toBuilder().precondition(precondition).build();

            // when & then
            ParseException exception = assertThrows(ParseException.class,
                    () -> apiTestParserService.parsePrecondition(requestDto));
            assertThat(exception.getMessage()).contains("Invalid step: abc");
        }

        @Test
        @DisplayName("잘못된 형식 - 여러 화살표")
        void parsePrecondition_MultipleArrows_ThrowsException() {
            // given
            String precondition = "step 1:header|test -> body|test -> path|test";
            requestDto = requestDto.toBuilder().precondition(precondition).build();

            // when & then
            ParseException exception = assertThrows(ParseException.class,
                    () -> apiTestParserService.parsePrecondition(requestDto));
            assertThat(exception.getMessage()).contains("Invalid action format");
        }

        @Test
        @DisplayName("잘못된 형식 - prev 부분에 파이프 없음")
        void parsePrecondition_MissingPipeInPrev_ThrowsException() {
            // given
            String precondition = "step 1:header -> body|test";
            requestDto = requestDto.toBuilder().precondition(precondition).build();

            // when & then
            ParseException exception = assertThrows(ParseException.class,
                    () -> apiTestParserService.parsePrecondition(requestDto));
            assertThat(exception.getMessage()).contains("Invalid prev, next format");
        }

        @Test
        @DisplayName("잘못된 형식 - next 부분에 파이프 없음")
        void parsePrecondition_MissingPipeInNext_ThrowsException() {
            // given
            String precondition = "step 1:header|test -> body";
            requestDto = requestDto.toBuilder().precondition(precondition).build();

            // when & then
            ParseException exception = assertThrows(ParseException.class,
                    () -> apiTestParserService.parsePrecondition(requestDto));
            assertThat(exception.getMessage()).contains("Invalid prev, next format");
        }

        @Test
        @DisplayName("Entity 없음")
        void parsePrecondition_EntityNotFound_ThrowsException() {
            // given
            String precondition = "step 1:header|test -> body|test";
            requestDto = requestDto.toBuilder().precondition(precondition).build();
            when(repo.findById("100-1-200")).thenReturn(Optional.empty());

            // when & then
            ParseException exception = assertThrows(ParseException.class,
                    () -> apiTestParserService.parsePrecondition(requestDto));
            assertThat(exception.getMessage()).contains("Not found ApiTestDetailEntity: 100-1-200");
        }
    }

    @Nested
    @DisplayName("빈 데이터 처리 테스트")
    class EmptyDataHandling {

        @Test
        @DisplayName("빈 header 데이터 처리")
        void parsePrecondition_EmptyHeader_HandledGracefully() throws ParseException {
            // given
            String precondition = "step 1:header|NonExistent -> body|test";
            requestDto = requestDto.toBuilder().precondition(precondition).build();

            // header가 비어있는 entity 생성
            ApiTestDetailRedisEntity emptyHeaderEntity = mockEntity.toBuilder()
                    .header(new LinkedMultiValueMap<>())
                    .build();
            when(repo.findById("100-1-200")).thenReturn(Optional.of(emptyHeaderEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.body()).isEmpty();
            assertThat(response.path()).isEmpty();
            assertThat(response.query()).isEmpty();
            assertThat(response.header()).isEmpty();
        }

        @Test
        @DisplayName("빈 body 데이터 처리")
        void parsePrecondition_EmptyBody_HandledGracefully() throws ParseException {
            // given
            String precondition = "step 1:body|NonExistent -> path|test";
            requestDto = requestDto.toBuilder().precondition(precondition).build();

            // body가 비어있는 entity 생성
            ApiTestDetailRedisEntity emptyBodyEntity = mockEntity.toBuilder()
                    .body(new HashMap<>())
                    .build();
            when(repo.findById("100-1-200")).thenReturn(Optional.of(emptyBodyEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.body()).isEmpty();
            assertThat(response.path()).isEmpty();
            assertThat(response.query()).isEmpty();
            assertThat(response.header()).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 키 참조")
        void parsePrecondition_NonExistentKey_HandledGracefully() throws ParseException {
            // given
            String precondition = "step 1:header|NonExistentKey -> body|test";
            requestDto = requestDto.toBuilder().precondition(precondition).build();
            when(repo.findById("100-1-200")).thenReturn(Optional.of(mockEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then - 존재하지 않는 키에 대해서는 아무것도 처리되지 않음
            assertThat(response.body()).isEmpty();
            assertThat(response.path()).isEmpty();
            assertThat(response.query()).isEmpty();
            assertThat(response.header()).isEmpty();
        }
    }

    @Nested
    @DisplayName("특수 케이스 테스트")
    class SpecialCases {

        @Test
        @DisplayName("잘못된 형식의 사전 조건 무시")
        void parsePrecondition_InvalidFormat_Skipped() throws ParseException {
            // given
            String precondition = "invalid format, step 1:body|token -> header|Authorization, another invalid";
            requestDto = requestDto.toBuilder().precondition(precondition).build();
            when(repo.findById("100-1-200")).thenReturn(Optional.of(mockEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then - 유효한 부분만 처리됨
            assertThat(response.header().get("Authorization")).containsExactly("abc123");
            assertThat(response.path()).isEmpty();
            assertThat(response.body()).isEmpty();
            assertThat(response.query()).isEmpty();
        }

        @Test
        @DisplayName("공백이 포함된 사전 조건")
        void parsePrecondition_WithWhitespace_Success() throws ParseException {
            // given
            String precondition = "  step 1:body|token -> header|Authorization  ,  step 1:path|userId -> query|user  ";
            requestDto = requestDto.toBuilder().precondition(precondition).build();
            when(repo.findById("100-1-200")).thenReturn(Optional.of(mockEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.header().get("Authorization")).containsExactly("abc123");
            assertThat(response.query().get("user")).containsExactly("12345");
        }

        @Test
        @DisplayName("빈 사전 조건 문자열")
        void parsePrecondition_EmptyPrecondition_ReturnsEmpty() throws ParseException {
            // given
            String precondition = "";
            requestDto = requestDto.toBuilder().precondition(precondition).build();

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.body()).isEmpty();
            assertThat(response.path()).isEmpty();
            assertThat(response.query()).isEmpty();
            assertThat(response.header()).isEmpty();
        }

        @Test
        @DisplayName("콤마만 있는 사전 조건")
        void parsePrecondition_OnlyCommas_ReturnsEmpty() throws ParseException {
            // given
            String precondition = ",,,";
            requestDto = requestDto.toBuilder().precondition(precondition).build();

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.body()).isEmpty();
            assertThat(response.path()).isEmpty();
            assertThat(response.query()).isEmpty();
            assertThat(response.header()).isEmpty();
        }
    }

    @Nested
    @DisplayName("데이터 타입별 변환 테스트")
    class DataTypeConversionTests {

        @Test
        @DisplayName("모든 데이터 타입 간 변환 테스트")
        void parsePrecondition_AllTypeConversions_Success() throws ParseException {
            // given
            String precondition = "step 1:header|Authorization -> body|auth, " +
                    "step 1:body|userId -> path|id, " +
                    "step 1:path|orderId -> query|order, " +
                    "step 1:query|page -> header|X-Page";
            requestDto = requestDto.toBuilder().precondition(precondition).build();
            when(repo.findById("100-1-200")).thenReturn(Optional.of(mockEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.body()).containsEntry("auth", "Bearer token123");
            assertThat(response.path()).containsEntry("id", "98765");
            assertThat(response.query().get("order")).containsExactly("67890");
            assertThat(response.header().get("X-Page")).containsExactly("1");
        }

        @Test
        @DisplayName("같은 타입 내에서의 데이터 복사")
        void parsePrecondition_SameTypeMapping_Success() throws ParseException {
            // given
            String precondition = "step 1:header|Authorization -> header|X-Auth, " +
                    "step 1:body|token -> body|authToken";
            requestDto = requestDto.toBuilder().precondition(precondition).build();
            when(repo.findById("100-1-200")).thenReturn(Optional.of(mockEntity));

            // when
            ApiTestParserServiceParsePreconditionResponseDto response = apiTestParserService
                    .parsePrecondition(requestDto);

            // then
            assertThat(response.header().get("X-Auth")).containsExactly("Bearer token123");
            assertThat(response.body()).containsEntry("authToken", "abc123");
            assertThat(response.path()).isEmpty();
            assertThat(response.query()).isEmpty();
        }
    }

    @Nested
    @DisplayName("BuildUri 테스트")
    class BuildUriTests {

        @Test
        @DisplayName("기본 URI - Path 변수 없음, Query 파라미터 없음")
        void buildUri_NoPathNoQuery_Success() {
            // given
            Map<String, String> path = new HashMap<>();
            MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
            ApiTestParserServiceBuildUriRequestDto requestDto = ApiTestParserServiceBuildUriRequestDto.builder()
                    .uri("/api/users")
                    .path(path)
                    .query(query)
                    .build();

            // when
            String result = apiTestParserService.buildUri(requestDto);

            // then
            assertThat(result).isEqualTo("/api/users");
        }

        @Test
        @DisplayName("Path 변수만 있는 경우")
        void buildUri_WithPathVariables_Success() {
            // given
            Map<String, String> path = new HashMap<>();
            path.put("userId", "12345");
            path.put("orderId", "67890");
            MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
            ApiTestParserServiceBuildUriRequestDto requestDto = ApiTestParserServiceBuildUriRequestDto.builder()
                    .uri("/api/users/{userId}/orders/{orderId}")
                    .path(path)
                    .query(query)
                    .build();

            // when
            String result = apiTestParserService.buildUri(requestDto);

            // then
            assertThat(result).isEqualTo("/api/users/12345/orders/67890");
        }

        @Test
        @DisplayName("Query 파라미터만 있는 경우 - 단일 값")
        void buildUri_WithSingleQueryParameters_Success() {
            // given
            Map<String, String> path = new HashMap<>();
            MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
            query.add("page", "1");
            query.add("size", "10");
            ApiTestParserServiceBuildUriRequestDto requestDto = ApiTestParserServiceBuildUriRequestDto.builder()
                    .uri("/api/users")
                    .path(path)
                    .query(query)
                    .build();

            // when
            String result = apiTestParserService.buildUri(requestDto);

            // then
            assertThat(result).isEqualTo("/api/users?page=1&size=10");
        }

        @Test
        @DisplayName("Query 파라미터만 있는 경우 - 다중 값")
        void buildUri_WithMultipleQueryParameters_Success() {
            // given
            Map<String, String> path = new HashMap<>();
            MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
            query.add("tag", "java");
            query.add("tag", "spring");
            query.add("category", "backend");
            ApiTestParserServiceBuildUriRequestDto requestDto = ApiTestParserServiceBuildUriRequestDto.builder()
                    .uri("/api/articles")
                    .path(path)
                    .query(query)
                    .build();

            // when
            String result = apiTestParserService.buildUri(requestDto);

            // then
            assertThat(result).startsWith("/api/articles?");
            assertThat(result).contains("tag=java");
            assertThat(result).contains("tag=spring");
            assertThat(result).contains("category=backend");
        }

        @Test
        @DisplayName("Path 변수와 Query 파라미터 모두 있는 경우")
        void buildUri_WithPathAndQuery_Success() {
            // given
            Map<String, String> path = new HashMap<>();
            path.put("userId", "12345");
            MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
            query.add("include", "profile");
            query.add("include", "settings");
            ApiTestParserServiceBuildUriRequestDto requestDto = ApiTestParserServiceBuildUriRequestDto.builder()
                    .uri("/api/users/{userId}")
                    .path(path)
                    .query(query)
                    .build();

            // when
            String result = apiTestParserService.buildUri(requestDto);

            // then
            assertThat(result).isEqualTo("/api/users/12345?include=profile&include=settings");
        }

        @Test
        @DisplayName("복잡한 Path 변수와 Query 파라미터")
        void buildUri_ComplexPathAndQuery_Success() {
            // given
            Map<String, String> path = new HashMap<>();
            path.put("companyId", "comp123");
            path.put("departmentId", "dept456");
            path.put("employeeId", "emp789");
            MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
            query.add("fields", "name");
            query.add("fields", "email");
            query.add("fields", "position");
            query.add("format", "json");
            ApiTestParserServiceBuildUriRequestDto requestDto = ApiTestParserServiceBuildUriRequestDto.builder()
                    .uri("/api/companies/{companyId}/departments/{departmentId}/employees/{employeeId}")
                    .path(path)
                    .query(query)
                    .build();

            // when
            String result = apiTestParserService.buildUri(requestDto);

            // then
            assertThat(result).isEqualTo(
                    "/api/companies/comp123/departments/dept456/employees/emp789?fields=name&fields=email&fields=position&format=json");
        }

        @Test
        @DisplayName("Path 변수가 존재하지 않는 경우 - 치환되지 않음")
        void buildUri_PathVariableNotProvided_RemainUnchanged() {
            // given
            Map<String, String> path = new HashMap<>();
            path.put("userId", "12345");
            // orderId는 제공되지 않음
            MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
            ApiTestParserServiceBuildUriRequestDto requestDto = ApiTestParserServiceBuildUriRequestDto.builder()
                    .uri("/api/users/{userId}/orders/{orderId}")
                    .path(path)
                    .query(query)
                    .build();

            // when
            String result = apiTestParserService.buildUri(requestDto);

            // then
            assertThat(result).isEqualTo("/api/users/12345/orders/{orderId}");
        }

        @Test
        @DisplayName("빈 Path와 빈 Query로 URI 빌드")
        void buildUri_EmptyPathAndQuery_Success() {
            // given
            Map<String, String> path = new HashMap<>();
            MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
            ApiTestParserServiceBuildUriRequestDto requestDto = ApiTestParserServiceBuildUriRequestDto.builder()
                    .uri("/api/health")
                    .path(path)
                    .query(query)
                    .build();

            // when
            String result = apiTestParserService.buildUri(requestDto);

            // then
            assertThat(result).isEqualTo("/api/health");
        }
    }
}
