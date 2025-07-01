package com.sk.skala.axcalibur.apitest.feature.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ExcuteTestServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ApiTestExecutionDataDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ParameterWithDataDto;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseResultEntity;
import com.sk.skala.axcalibur.apitest.feature.repository.MappingRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.ParameterRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.ScenarioRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseResultRepository;
import com.sk.skala.axcalibur.apitest.global.exception.BusinessExceptionHandler;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApiTestService excuteTestService 메서드 테스트")
public class ApiTestServiceTest {

    @Mock
    private TestcaseRepository tc;

    @Mock
    private TestcaseResultRepository tr;

    @Mock
    private ScenarioRepository scene;

    @Mock
    private MappingRepository mr;

    @Mock
    private ParameterRepository pr;

    @Mock
    private RedisTemplate<String, Object> redis;

    @Mock
    private StreamOperations<String, Object, Object> streamOperations;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private ApiTestServiceImpl apiTestService;

    private ExcuteTestServiceRequestDto testRequestDto;
    private List<ApiTestExecutionDataDto> mockExecutionDataList;
    private List<TestcaseResultEntity> mockTestcaseResultList;
    private List<ParameterWithDataDto> mockParametersWithData;

    @BeforeEach
    void setUp() {
        // Redis StreamOperations Mock 설정 (lenient로 설정하여 모든 테스트에서 사용되지 않아도 오류 방지)
        lenient().when(redis.opsForStream()).thenReturn(streamOperations);

        // 테스트 요청 DTO 설정
        testRequestDto = ExcuteTestServiceRequestDto.builder()
                .projectKey(1)
                .scenarioList(List.of("scenario-1", "scenario-2"))
                .build();

        // Mock 실행 데이터 설정
        mockExecutionDataList = List.of(
                ApiTestExecutionDataDto.builder()
                        .mappingId(100)
                        .step(1)
                        .testcaseId(1001)
                        .testcaseStringId("TC-1001")
                        .precondition("step 1:header|Authorization -> body|token")
                        .status(200)
                        .apiListId(2001)
                        .method("GET")
                        .url("/api/test")
                        .path("/api/test")
                        .build(),
                ApiTestExecutionDataDto.builder()
                        .mappingId(101)
                        .step(2)
                        .testcaseId(1002)
                        .testcaseStringId("TC-1002")
                        .precondition("")
                        .status(201)
                        .apiListId(2002)
                        .method("POST")
                        .url("/api/create")
                        .path("/api/create")
                        .build());

        // Mock 테스트케이스 결과 설정
        mockTestcaseResultList = List.of(
                TestcaseResultEntity.builder()
                        .id(3001)
                        .testcase(TestcaseEntity.builder().id(1001).build())
                        .result("")
                        .success(false)
                        .time(null)
                        .reason(null)
                        .build(),
                TestcaseResultEntity.builder()
                        .id(3002)
                        .testcase(TestcaseEntity.builder().id(1002).build())
                        .result("")
                        .success(false)
                        .time(null)
                        .reason(null)
                        .build());

        // Mock 파라미터 데이터 설정
        mockParametersWithData = List.of(
                ParameterWithDataDto.builder()
                        .parameterId(1)
                        .parameterName("Authorization")
                        .dataType("string")
                        .apiListId(2001)
                        .categoryName("request")
                        .contextName("header")
                        .parentId(null)
                        .testcaseId(1001)
                        .value("Bearer test-token")
                        .build(),
                ParameterWithDataDto.builder()
                        .parameterId(2)
                        .parameterName("Content-Type")
                        .dataType("string")
                        .apiListId(2002)
                        .categoryName("request")
                        .contextName("header")
                        .parentId(null)
                        .testcaseId(1002)
                        .value("application/json")
                        .build());
    }

    @Nested
    @DisplayName("정상 케이스 테스트")
    class SuccessfulCases {

        @Test
        @DisplayName("정상적인 시나리오 목록으로 테스트 실행 성공")
        void excuteTestService_WithValidScenarios_ShouldReturnProcessedTestcaseIds() {
            // given
            when(mr.findExecutionDataByProjectAndScenarios(testRequestDto.projectKey(), testRequestDto.scenarioList()))
                    .thenReturn(mockExecutionDataList);
            when(tr.saveAll(anyList())).thenReturn(mockTestcaseResultList);
            when(pr.findParametersWithDataByApiListAndTestcase(anyList(), anyList()))
                    .thenReturn(mockParametersWithData);

            // Redis Stream Mock 설정
            when(streamOperations.add(any(MapRecord.class))).thenReturn(RecordId.of("1234567890-0"));

            // when
            List<String> result = apiTestService.excuteTestService(testRequestDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder("TC-1001", "TC-1002");

            // 의존성 호출 검증
            verify(mr, times(1)).findExecutionDataByProjectAndScenarios(testRequestDto.projectKey(),
                    testRequestDto.scenarioList());
            verify(tr, times(1)).saveAll(anyList());
            verify(pr, times(1)).findParametersWithDataByApiListAndTestcase(anyList(), anyList());
            verify(streamOperations, times(2)).add(any(MapRecord.class));
        }

        @Test
        @DisplayName("단일 시나리오로 테스트 실행 성공")
        void excuteTestService_WithSingleScenario_ShouldReturnSingleTestcaseId() {
            // given
            ExcuteTestServiceRequestDto singleScenarioDto = ExcuteTestServiceRequestDto.builder()
                    .projectKey(1)
                    .scenarioList(List.of("scenario-1"))
                    .build();

            List<ApiTestExecutionDataDto> singleExecutionData = List.of(mockExecutionDataList.get(0));
            List<TestcaseResultEntity> singleTestcaseResult = List.of(mockTestcaseResultList.get(0));
            List<ParameterWithDataDto> singleParameterData = List.of(mockParametersWithData.get(0));

            when(mr.findExecutionDataByProjectAndScenarios(1, List.of("scenario-1")))
                    .thenReturn(singleExecutionData);
            when(tr.saveAll(anyList())).thenReturn(singleTestcaseResult);
            when(pr.findParametersWithDataByApiListAndTestcase(anyList(), anyList()))
                    .thenReturn(singleParameterData);

            when(streamOperations.add(any(MapRecord.class))).thenReturn(RecordId.of("1234567890-0"));

            // when
            List<String> result = apiTestService.excuteTestService(singleScenarioDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result).containsExactly("TC-1001");

            verify(mr, times(1)).findExecutionDataByProjectAndScenarios(1, List.of("scenario-1"));
            verify(tr, times(1)).saveAll(anyList());
            verify(pr, times(1)).findParametersWithDataByApiListAndTestcase(anyList(), anyList());
            verify(streamOperations, times(1)).add(any(MapRecord.class));
        }
    }

    @Nested
    @DisplayName("예외 케이스 테스트")
    class ExceptionCases {

        @Test
        @DisplayName("빈 시나리오 목록으로 테스트 실행 시 예외 발생")
        void excuteTestService_WithEmptyScenarios_ShouldThrowException() {
            // given
            ExcuteTestServiceRequestDto emptyScenarioDto = ExcuteTestServiceRequestDto.builder()
                    .projectKey(1)
                    .scenarioList(List.of())
                    .build();

            // when & then
            assertThatThrownBy(() -> apiTestService.excuteTestService(emptyScenarioDto))
                    .isInstanceOf(BusinessExceptionHandler.class)
                    .hasMessageContaining("No scenarios provided for api-test execution");

            // Mock 호출 검증 - 아무것도 호출되지 않아야 함
            verify(mr, never()).findExecutionDataByProjectAndScenarios(any(), any());
            verify(tr, never()).saveAll(any());
            verify(pr, never()).findParametersWithDataByApiListAndTestcase(any(), any());
        }

        @Test
        @DisplayName("실행 데이터가 없을 때 예외 발생")
        void excuteTestService_WithNoExecutionData_ShouldThrowException() {
            // given
            when(mr.findExecutionDataByProjectAndScenarios(testRequestDto.projectKey(), testRequestDto.scenarioList()))
                    .thenReturn(List.of());

            // when & then
            assertThatThrownBy(() -> apiTestService.excuteTestService(testRequestDto))
                    .isInstanceOf(BusinessExceptionHandler.class)
                    .hasMessageContaining("Error processing test execution");

            verify(mr, times(1)).findExecutionDataByProjectAndScenarios(testRequestDto.projectKey(),
                    testRequestDto.scenarioList());
            verify(tr, never()).saveAll(any());
            verify(pr, never()).findParametersWithDataByApiListAndTestcase(any(), any());
        }

        @Test
        @DisplayName("Redis Stream 추가 실패 시 로그 기록 및 정상 처리")
        void excuteTestService_WithRedisStreamFailure_ShouldLogErrorAndContinue() {
            // given
            when(mr.findExecutionDataByProjectAndScenarios(testRequestDto.projectKey(), testRequestDto.scenarioList()))
                    .thenReturn(mockExecutionDataList);
            when(tr.saveAll(anyList())).thenReturn(mockTestcaseResultList);
            when(pr.findParametersWithDataByApiListAndTestcase(anyList(), anyList()))
                    .thenReturn(mockParametersWithData);

            // 첫 번째 호출은 성공, 두 번째 호출은 실패
            when(streamOperations.add(any(MapRecord.class)))
                    .thenReturn(RecordId.of("1234567890-0"))
                    .thenReturn(null);

            // when
            List<String> result = apiTestService.excuteTestService(testRequestDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1); // 성공한 것만 반환
            assertThat(result).containsExactly("TC-1001");

            verify(streamOperations, times(2)).add(any(MapRecord.class));
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    class BoundaryValueTests {

        @Test
        @DisplayName("대량의 시나리오 처리 테스트")
        void excuteTestService_WithManyScenarios_ShouldHandleCorrectly() {
            // given
            List<String> manyScenarios = new ArrayList<>();
            List<ApiTestExecutionDataDto> manyExecutionData = new ArrayList<>();
            List<TestcaseResultEntity> manyTestcaseResults = new ArrayList<>();
            List<ParameterWithDataDto> manyParameterData = new ArrayList<>();

            for (int i = 1; i <= 10; i++) {
                manyScenarios.add("scenario-" + i);
                manyExecutionData.add(ApiTestExecutionDataDto.builder()
                        .mappingId(100 + i)
                        .step(1)
                        .testcaseId(1000 + i)
                        .testcaseStringId("TC-" + (1000 + i))
                        .precondition("")
                        .status(200)
                        .apiListId(2000 + i)
                        .method("GET")
                        .url("/api/test/" + i)
                        .path("/api/test/" + i)
                        .build());
                manyTestcaseResults.add(TestcaseResultEntity.builder()
                        .id(3000 + i)
                        .testcase(TestcaseEntity.builder().id(1000 + i).build())
                        .result("")
                        .success(false)
                        .time(null)
                        .reason(null)
                        .build());
                manyParameterData.add(ParameterWithDataDto.builder()
                        .parameterId(i)
                        .parameterName("param-" + i)
                        .dataType("string")
                        .apiListId(2000 + i)
                        .categoryName("request")
                        .contextName("query")
                        .parentId(null)
                        .testcaseId(1000 + i)
                        .value("value-" + i)
                        .build());
            }

            ExcuteTestServiceRequestDto manyScenarioDto = ExcuteTestServiceRequestDto.builder()
                    .projectKey(1)
                    .scenarioList(manyScenarios)
                    .build();

            when(mr.findExecutionDataByProjectAndScenarios(1, manyScenarios))
                    .thenReturn(manyExecutionData);
            when(tr.saveAll(anyList())).thenReturn(manyTestcaseResults);
            when(pr.findParametersWithDataByApiListAndTestcase(anyList(), anyList()))
                    .thenReturn(manyParameterData);

            when(streamOperations.add(any(MapRecord.class))).thenReturn(RecordId.of("1234567890-0"));

            // when
            List<String> result = apiTestService.excuteTestService(manyScenarioDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(10);
            for (int i = 1; i <= 10; i++) {
                assertThat(result).contains("TC-" + (1000 + i));
            }

            verify(streamOperations, times(10)).add(any(MapRecord.class));
        }
    }

    @Nested
    @DisplayName("파라미터 처리 테스트")
    class ParameterProcessingTests {

        @Test
        @DisplayName("파라미터가 없는 테스트케이스 처리")
        void excuteTestService_WithNoParameters_ShouldProcessCorrectly() {
            // given
            when(mr.findExecutionDataByProjectAndScenarios(testRequestDto.projectKey(), testRequestDto.scenarioList()))
                    .thenReturn(mockExecutionDataList);
            when(tr.saveAll(anyList())).thenReturn(mockTestcaseResultList);
            when(pr.findParametersWithDataByApiListAndTestcase(anyList(), anyList()))
                    .thenReturn(List.of()); // 빈 파라미터 목록

            when(streamOperations.add(any(MapRecord.class))).thenReturn(RecordId.of("1234567890-0"));

            // when
            List<String> result = apiTestService.excuteTestService(testRequestDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder("TC-1001", "TC-1002");

            verify(pr, times(1)).findParametersWithDataByApiListAndTestcase(anyList(), anyList());
            verify(streamOperations, times(2)).add(any(MapRecord.class));
        }

        @Test
        @DisplayName("다양한 타입의 파라미터 처리")
        void excuteTestService_WithVariousParameterTypes_ShouldProcessCorrectly() {
            // given
            List<ParameterWithDataDto> variousParameterData = List.of(
                    ParameterWithDataDto.builder()
                            .parameterId(1)
                            .parameterName("Authorization")
                            .dataType("string")
                            .apiListId(2001)
                            .categoryName("request")
                            .contextName("header")
                            .parentId(null)
                            .testcaseId(1001)
                            .value("Bearer token")
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(2)
                            .parameterName("userId")
                            .dataType("integer")
                            .apiListId(2001)
                            .categoryName("request")
                            .contextName("path")
                            .parentId(null)
                            .testcaseId(1001)
                            .value("12345")
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(3)
                            .parameterName("page")
                            .dataType("integer")
                            .apiListId(2001)
                            .categoryName("request")
                            .contextName("query")
                            .parentId(null)
                            .testcaseId(1001)
                            .value("1")
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(4)
                            .parameterName("requestBody")
                            .dataType("object")
                            .apiListId(2001)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(null)
                            .testcaseId(1001)
                            .value("{\"name\": \"test\", \"age\": 30}")
                            .build(),
                    // requestBody 객체의 자식 파라미터들
                    ParameterWithDataDto.builder()
                            .parameterId(5)
                            .parameterName("name")
                            .dataType("string")
                            .apiListId(2001)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(4)
                            .testcaseId(1001)
                            .value("test")
                            .build(),
                    ParameterWithDataDto.builder()
                            .parameterId(6)
                            .parameterName("age")
                            .dataType("integer")
                            .apiListId(2001)
                            .categoryName("request")
                            .contextName("body")
                            .parentId(4)
                            .testcaseId(1001)
                            .value("30")
                            .build());

            when(mr.findExecutionDataByProjectAndScenarios(testRequestDto.projectKey(), testRequestDto.scenarioList()))
                    .thenReturn(mockExecutionDataList);
            when(tr.saveAll(anyList())).thenReturn(mockTestcaseResultList);
            when(pr.findParametersWithDataByApiListAndTestcase(anyList(), anyList()))
                    .thenReturn(variousParameterData);

            when(streamOperations.add(any(MapRecord.class))).thenReturn(RecordId.of("1234567890-0"));

            // when
            List<String> result = apiTestService.excuteTestService(testRequestDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder("TC-1001", "TC-1002");

            verify(pr, times(1)).findParametersWithDataByApiListAndTestcase(anyList(), anyList());
            verify(streamOperations, times(2)).add(any(MapRecord.class));
        }
    }

    @Nested
    @DisplayName("데이터 변환 테스트")
    class DataConversionTests {

        @Test
        @DisplayName("buildTaskData 메서드 호출 검증")
        void excuteTestService_ShouldCallBuildTaskDataCorrectly() {
            // given
            when(mr.findExecutionDataByProjectAndScenarios(testRequestDto.projectKey(), testRequestDto.scenarioList()))
                    .thenReturn(mockExecutionDataList);
            when(tr.saveAll(anyList())).thenReturn(mockTestcaseResultList);
            when(pr.findParametersWithDataByApiListAndTestcase(anyList(), anyList()))
                    .thenReturn(mockParametersWithData);

            when(streamOperations.add(any(MapRecord.class))).thenReturn(RecordId.of("1234567890-0"));

            // when
            List<String> result = apiTestService.excuteTestService(testRequestDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);

            // buildTaskData 메서드가 각 테스트케이스마다 호출되는지 확인 (간접적으로)
            verify(pr, times(1)).findParametersWithDataByApiListAndTestcase(anyList(), anyList());
        }

        @Test
        @DisplayName("ApiTaskDto 생성 및 Redis Stream 전송 확인")
        void excuteTestService_ShouldCreateApiTaskDtoAndSendToRedisStream() {
            // given
            when(mr.findExecutionDataByProjectAndScenarios(testRequestDto.projectKey(), testRequestDto.scenarioList()))
                    .thenReturn(mockExecutionDataList);
            when(tr.saveAll(anyList())).thenReturn(mockTestcaseResultList);
            when(pr.findParametersWithDataByApiListAndTestcase(anyList(), anyList()))
                    .thenReturn(mockParametersWithData);

            when(streamOperations.add(any(MapRecord.class))).thenReturn(RecordId.of("1234567890-0"));

            // when
            List<String> result = apiTestService.excuteTestService(testRequestDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);

            // Redis Stream에 MapRecord가 추가되는지 확인
            verify(streamOperations, times(2)).add(any(MapRecord.class));
        }
    }

    @Nested
    @DisplayName("예외 상황 처리 테스트")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("Repository 예외 발생 시 처리")
        void excuteTestService_WithRepositoryException_ShouldThrowBusinessException() {
            // given
            when(mr.findExecutionDataByProjectAndScenarios(testRequestDto.projectKey(), testRequestDto.scenarioList()))
                    .thenThrow(new RuntimeException("Database connection error"));

            // when & then
            assertThatThrownBy(() -> apiTestService.excuteTestService(testRequestDto))
                    .isInstanceOf(BusinessExceptionHandler.class)
                    .hasMessageContaining("Error processing test execution");

            verify(mr, times(1)).findExecutionDataByProjectAndScenarios(testRequestDto.projectKey(),
                    testRequestDto.scenarioList());
            verify(tr, never()).saveAll(any());
            verify(pr, never()).findParametersWithDataByApiListAndTestcase(any(), any());
        }

        @Test
        @DisplayName("null 프로젝트 키로 요청 시 예외 처리")
        void excuteTestService_WithNullProjectKey_ShouldHandleGracefully() {
            // given
            ExcuteTestServiceRequestDto nullProjectKeyDto = ExcuteTestServiceRequestDto.builder()
                    .projectKey(null)
                    .scenarioList(List.of("scenario-1"))
                    .build();

            // when & then
            // 실제 서비스 로직에서 null 체크가 있다면 적절한 예외가 발생해야 함
            // 여기서는 Repository 계층에서 예외가 발생할 것으로 예상
            assertThatThrownBy(() -> apiTestService.excuteTestService(nullProjectKeyDto))
                    .isInstanceOf(BusinessExceptionHandler.class);
        }
    }
}
