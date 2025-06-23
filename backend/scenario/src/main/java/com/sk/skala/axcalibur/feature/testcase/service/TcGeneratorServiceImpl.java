package com.sk.skala.axcalibur.feature.testcase.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.testcase.client.FastApiClient;
import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;
import com.sk.skala.axcalibur.feature.testcase.dto.request.TcRequestPayload;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TestcaseDataDto;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TestcaseGenerationResponse;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TestcaseParamDto;
import com.sk.skala.axcalibur.feature.testcase.entity.ApiListEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.CategoryEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.ContextEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.MappingEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.ParameterEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.ScenarioEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.TestCaseDataEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.TestCaseEntity;
import com.sk.skala.axcalibur.feature.testcase.repository.CategoryRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.ContextRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.MappingRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.ParameterRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.TestCaseDataRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.TestCaseRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;

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
    
    private final CategoryRepository categoryRepository;
    private final ContextRepository contextRepository;
    private final MappingRepository mappingRepository;
    private final ParameterRepository parameterRepository;
    private final TestCaseDataRepository testcaseDataRepository;
    private final TestCaseRepository testcaseRepository;
    
    // FastAPI로 생성 요청 전송하는 함수
    @Override
    public TestcaseGenerationResponse callFastApi(TcRequestPayload payload, ScenarioEntity scenario) {
        try {
            TestcaseGenerationResponse response = tcGeneratorClient.generate(scenario.getScenarioId(), payload);
        
            if (response == null || response.getTcList() == null || response.getTcList().isEmpty()) {
                log.error("FastAPI 응답이 비정상적으로 null이거나 비어 있음 (scenarioId: {})", scenario.getScenarioId());
                throw new BusinessExceptionHandler("FastAPI 응답이 유효하지 않습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            }

            return response;

        } catch (IllegalArgumentException e) {
            log.error("FastAPI 요청 매핑 오류: {}", e.getMessage(), e);
            throw new BusinessExceptionHandler("FastAPI 요청 구성에 문제가 있습니다.", ErrorCode.BAD_REQUEST_ERROR);
        } catch (RetryableException e) {
            // timeout 시 발생
            log.error("FastAPI 호출 타임아웃 (scenarioId: {})", scenario.getScenarioId(), e);
            throw new BusinessExceptionHandler(
                "FastAPI 호출 타임아웃 (시나리오 ID: " + scenario.getScenarioId() + ")",
                ErrorCode.GATEWAY_TIMEOUT_ERROR // 필요 시 정의
            );
        } catch (FeignException e) {
            log.error("FastAPI 호출 실패: {}", e.getMessage(), e);
            throw new BusinessExceptionHandler("FastAPI 호출 중 오류", ErrorCode.INTERNAL_SERVER_ERROR);
        } 
    }
    
    // fastAPI로부터 생성 내용 응답 받아 저장하는 함수
    @Transactional
    @Override
    public void saveTestcases(TestcaseGenerationResponse response) {
        // Set과 Map으로 저장할 데이터들이 참조할 데이터의 ID와 데이터를 한번에 캐싱해두는 방식

        // 1. category/context 캐싱
        Map<String, CategoryEntity> categoryMap = categoryRepository.findAll().stream()
            .collect(Collectors.toMap(c -> c.getName().trim(), Function.identity()));
        Map<String, ContextEntity> contextMap = contextRepository.findAll().stream()
            .collect(Collectors.toMap(ContextEntity::getName, Function.identity()));

        // 2. Parameter 테이블로부터 사용할 paramId들 조회
        Set<Integer> allParamIds = response.getTcList().stream()
            .flatMap(tc -> tc.getTestDataList().stream())
            .map(dto -> dto.getParam().getParamId())
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Map<Integer, ParameterEntity> paramIdMap = parameterRepository.findAllById(allParamIds).stream()
            .collect(Collectors.toMap(ParameterEntity::getId, Function.identity()));

        // 3. 매핑표로부터 매핑 정보 및 사용한 API ID 조회
        Set<Integer> mappingIds = response.getTcList().stream()
            .map(TestcaseDataDto::getMappingId)
            .collect(Collectors.toSet());

        Map<Integer, MappingEntity> mappingMap = mappingRepository.findAllById(mappingIds).stream()
            .collect(Collectors.toMap(MappingEntity::getId, Function.identity()));

        Set<Integer> apiIds = mappingMap.values().stream()
            .map(m -> m.getApiListKey().getId())
            .collect(Collectors.toSet());

        Map<Integer, Map<String, ParameterEntity>> paramCacheByApiId = parameterRepository.findByApiListKey_IdIn(new ArrayList<>(apiIds)).stream()
            .collect(Collectors.groupingBy(
                p -> p.getApiListKey().getId(),
                Collectors.toMap(ParameterEntity::getName, Function.identity(), (p1, p2) -> p1)
            ));

        // 모을 리스트들
        List<TestCaseEntity> testcaseList = new ArrayList<>();
        List<ParameterEntity> newParamList = new ArrayList<>();
        List<ParameterEntity> paramListWithParent = new ArrayList<>();
        List<TestCaseDataEntity> dataList = new ArrayList<>();

        for (TestcaseDataDto tcData : response.getTcList()) {
            MappingEntity mapping = mappingMap.get(tcData.getMappingId());
            if (mapping == null) throw new BusinessExceptionHandler("매핑 정보 없음", ErrorCode.NOT_FOUND_ERROR);

            ApiListEntity apiList = mapping.getApiListKey();
            Map<String, ParameterEntity> existingParams = paramCacheByApiId.getOrDefault(apiList.getId(), new HashMap<>());

            // TestCase 수집
            TestCaseEntity testcase = TestCaseEntity.builder()
                .testcaseId(tcData.getTcId())
                .description(tcData.getDescription())
                .precondition(tcData.getPrecondition())
                .expected(tcData.getExpectedResult())
                .status(tcData.getStatus())
                .mappingKey(mapping)
                .build();
            testcaseList.add(testcase);

            // parent 정보 저장을 위한 map 생성
            Map<String, ParameterEntity> savedParamMap = new HashMap<>();

            // TC 별 Parameter 정보 수집
            for (TestcaseParamDto paramDto : tcData.getTestDataList()) {
                ApiParamDto param = paramDto.getParam();
                validateParam(param);

                CategoryEntity category = categoryMap.get(param.getCategory().trim());
                if (category == null) throw new BusinessExceptionHandler("카테고리 없음: " + param.getCategory(), ErrorCode.NOT_FOUND_ERROR);
                ContextEntity context = contextMap.get(param.getContext());
                if (context == null) throw new BusinessExceptionHandler("컨텍스트 없음: " + param.getContext(), ErrorCode.NOT_FOUND_ERROR);

                ParameterEntity parameter;

                if (param.getParamId() != null) {
                    parameter = paramIdMap.get(param.getParamId());
                    if (parameter == null) throw new BusinessExceptionHandler("파라미터 ID 오류: " + param.getParamId(), ErrorCode.NOT_FOUND_ERROR);
                } else {
                    parameter = existingParams.get(param.getName());
                    if (parameter == null) {
                        parameter = ParameterEntity.builder()
                            .nameKo(param.getKoName())
                            .name(param.getName())
                            .dataType(param.getType())
                            .length(param.getLength())
                            .format(param.getFormat())
                            .defaultValue(param.getDefaultValue())
                            .required(param.getRequired())
                            .description(param.getDesc())
                            .categoryKey(category)
                            .contextKey(context)
                            .apiListKey(apiList)
                            .build();
                        newParamList.add(parameter);
                        existingParams.put(param.getName(), parameter); // 캐시에 추가
                    }
                }

                savedParamMap.put(param.getName(), parameter);
            }

            // parentKey 및 TestCaseData 수집
            for (TestcaseParamDto paramDto : tcData.getTestDataList()) {
                ApiParamDto param = paramDto.getParam();
                ParameterEntity parameter = savedParamMap.get(param.getName());
                
                String rawParent = param.getParent();
                if (rawParent != null && !rawParent.trim().isEmpty() && !"null".equalsIgnoreCase(rawParent.trim())) { // parent 항목이 null이 아닌데 조회되는 파라미터 항목이 없음
                    String trimmedParent = rawParent.trim();
                    ParameterEntity parent = savedParamMap.get(trimmedParent);
                    
                    if (parent == null) {
                        throw new BusinessExceptionHandler(
                            "parent 값은 존재하나 해당 키에 해당하는 파라미터가 존재하지 않음: " + param.getParent(),
                            ErrorCode.NOT_FOUND_ERROR
                        );
                    }
                    parameter.setParentKey(parent);
                    paramListWithParent.add(parameter);
                }

                dataList.add(TestCaseDataEntity.builder()
                    .testcaseKey(testcase)
                    .parameterKey(parameter)
                    .value(paramDto.getValue())
                    .build());
            }
        }

        // 일괄 저장
        testcaseRepository.saveAll(testcaseList);
        parameterRepository.saveAll(newParamList); // parentKey 없이 먼저 저장
        parameterRepository.saveAll(paramListWithParent); // parentKey 설정 후 재저장
        testcaseDataRepository.saveAll(dataList);
    }

    // Not Null이여야되는 파라미터 항목 여부 검증
    private void validateParam(ApiParamDto param) {
        if (param.getName() == null || param.getName().isBlank()) {
            throw new BusinessExceptionHandler("파라미터 name 누락", ErrorCode.BAD_REQUEST_ERROR);
        }
        if (param.getType() == null) {
            throw new BusinessExceptionHandler("파라미터 type 누락", ErrorCode.BAD_REQUEST_ERROR);
        }
        if (param.getRequired() == null) {
            throw new BusinessExceptionHandler("파라미터 필수 여부 누락", ErrorCode.BAD_REQUEST_ERROR);
        }
        if (param.getCategory() == null || param.getCategory().isBlank()) {
            throw new BusinessExceptionHandler("파라미터 category 누락", ErrorCode.BAD_REQUEST_ERROR);
        }
        if (param.getContext() == null || param.getContext().isBlank()) {
            throw new BusinessExceptionHandler("파라미터 context 누락", ErrorCode.BAD_REQUEST_ERROR);
        }
    }
}
