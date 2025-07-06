package com.sk.skala.axcalibur.feature.testcase.service;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.testcase.client.FastApiClient;
import com.sk.skala.axcalibur.feature.testcase.dto.request.TcGenerationRequest;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcGeneratedDataDto;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcGenerationResponse;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcParamDto;
import com.sk.skala.axcalibur.global.repository.MappingRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.MappingEntity;
import com.sk.skala.axcalibur.global.entity.ParameterEntity;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.entity.TestCaseDataEntity;
import com.sk.skala.axcalibur.global.entity.TestCaseEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.ParameterRepository;
import com.sk.skala.axcalibur.global.repository.TestCaseDataRepository;
import com.sk.skala.axcalibur.global.repository.TestCaseRepository;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TC 생성 단계의 서비스
 * 조합된 request 객체를 fastAPI에 보내고,
 * 생성 이후 받은 응답을 DB에 저장하는 내용을 실제 구현합니다.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class TcGeneratorServiceImpl implements TcGeneratorService {
    private final FastApiClient tcGeneratorClient;

    private final MappingRepository mappingRepository;
    private final ParameterRepository parameterRepository;
    private final TestCaseDataRepository testcaseDataRepository;
    private final TestCaseRepository testcaseRepository;
    
    // FastAPI로 생성 요청 전송하는 함수
    @Override
    public TcGenerationResponse callFastApi(TcGenerationRequest payload, ScenarioEntity scenario) {
        try {
            TcGenerationResponse response = tcGeneratorClient.generate(scenario.getScenarioId(), payload);
        
            if (response == null || response.getTcList() == null || response.getTcList().isEmpty()) {
                log.error("FastAPI 응답이 비정상적으로 null이거나 비어 있음 (scenarioId: {})", scenario.getScenarioId());
                throw new BusinessExceptionHandler("FastAPI 응답이 유효하지 않습니다.", ErrorCode.NOT_VALID_ERROR);
            }

            return response;

        } catch (IllegalArgumentException e) {
            log.error("FastAPI 요청 매핑 오류 (scenarioId: {}): {}", scenario.getScenarioId(), e.getMessage(), e);
            throw new BusinessExceptionHandler(
                "FastAPI 요청 구성 오류 (시나리오 ID: " + scenario.getScenarioId() + ")",
                ErrorCode.BAD_REQUEST_ERROR
            );
        }catch (RetryableException e) {
            // timeout 시 발생
            Throwable cause = e.getCause();

            if (cause instanceof SocketTimeoutException) {
                // 응답이 느린 경우
                log.error("FastAPI 호출 타임아웃 (scenarioId: {})", scenario.getScenarioId(), e);
                throw new BusinessExceptionHandler(
                    "FastAPI 호출 타임아웃 (시나리오 ID: " + scenario.getScenarioId() + ")",
                    ErrorCode.GATEWAY_TIMEOUT_ERROR // 필요 시 정의
                );
            } else if (cause instanceof ConnectException) {
                // 서버가 아예 죽어서 접속이 안 되는 경우
                throw new BusinessExceptionHandler(
                    "FastAPI 호출 중 오류: " + scenario.getScenarioId(),
                    ErrorCode.INTERNAL_SERVER_ERROR
                );
            } else {
                // 기타 네트워크 문제
                log.error("FastAPI 기타 네트워크 오류 (scenarioId: {}): {}", scenario.getScenarioId(), cause.getMessage(), e);
                throw new BusinessExceptionHandler(
                    "FastAPI 기타 네트워크 오류 (시나리오 ID: " + scenario.getScenarioId() + ")",
                    ErrorCode.INTERNAL_SERVER_ERROR
                );
            }
        } catch (FeignException e) {
            log.error("FastAPI 호출 실패: {}", e.getMessage(), e);
            throw new BusinessExceptionHandler("FastAPI 호출 중 오류", ErrorCode.INTERNAL_SERVER_ERROR);
        } 
    }
    
    // fastAPI로부터 생성 내용 응답 받아 저장하는 함수
    @Transactional
    @Override
    public void saveTestcases(TcGenerationResponse response) {
        // Set과 Map으로 저장할 데이터들이 참조할 데이터의 ID와 데이터를 한번에 캐싱해두는 방식
        // 응답에서 쓰인 paramId, mappingId만 한 번에 조회
        Set<Integer> allParamIds = response.getTcList().stream()
            .flatMap(tc -> tc.getTestDataList().stream())
            .map(dto -> dto.getParamId())
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Map<Integer, ParameterEntity> paramIdMap = parameterRepository.findAllById(allParamIds).stream()
            .collect(Collectors.toMap(ParameterEntity::getId, Function.identity()));

        Set<Integer> mappingIds = response.getTcList().stream()
            .map(TcGeneratedDataDto::getMappingId)
            .collect(Collectors.toSet());

        Map<Integer, MappingEntity> mappingMap = mappingRepository.findAllById(mappingIds).stream()
            .collect(Collectors.toMap(MappingEntity::getId, Function.identity()));

        // 결과를 담을 리스트 정의
        List<TestCaseEntity> testcaseList = new ArrayList<>();
        List<TestCaseDataEntity> testcaseDataList = new ArrayList<>();

        for (TcGeneratedDataDto tcData : response.getTcList()) {
            // 매핑표 검증
            MappingEntity mapping = mappingMap.get(tcData.getMappingId());
            if (mapping == null) {
                throw new BusinessExceptionHandler("매핑 정보 없음: " + tcData.getMappingId(), ErrorCode.NOT_FOUND_ERROR);
            }

            TestCaseEntity testcase = TestCaseEntity.builder()
                .testcaseId(tcData.getTcId())
                .description(tcData.getDescription())
                .precondition(tcData.getPrecondition())
                .expected(tcData.getExpectedResult())
                .status(tcData.getStatus())
                .mappingKey(mapping)
                .build();
            testcaseList.add(testcase);

            for (TcParamDto paramDto : tcData.getTestDataList()) {
                Integer paramId = paramDto.getParamId();
                if (paramId == null || !paramIdMap.containsKey(paramId))
                    throw new BusinessExceptionHandler("파라미터 ID 오류: " + paramId, ErrorCode.NOT_FOUND_ERROR);

                ParameterEntity parameter = paramIdMap.get(paramId);

                // 현재 파라미터가 이 TC의 API에 속하는가 여부 검증
                // AI 단에서 검증 진행함으로 주석처리
                // if (!parameter.getApiListKey().getId().equals(mapping.getApiListKey().getId())) {
                //     throw new BusinessExceptionHandler(
                //         "파라미터가 이 테스트케이스의 API에 속하지 않음. paramId: " + paramId,
                //         ErrorCode.NOT_VALID_ERROR
                //     );
                // }

                testcaseDataList.add(TestCaseDataEntity.builder()
                    .testcaseKey(testcase)
                    .parameterKey(parameter)
                    .value(paramDto.getValue())
                    .build());
            }
        }

        // 결과 일괄 저장
        testcaseRepository.saveAll(testcaseList);
        testcaseDataRepository.saveAll(testcaseDataList);
    }
}
