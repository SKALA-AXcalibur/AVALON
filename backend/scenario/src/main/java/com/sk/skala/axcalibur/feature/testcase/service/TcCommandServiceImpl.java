package com.sk.skala.axcalibur.feature.testcase.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.sk.skala.axcalibur.feature.testcase.dto.request.TcUpdateRequest;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcParamDto;
import com.sk.skala.axcalibur.global.repository.ApiListRepository;
import com.sk.skala.axcalibur.global.repository.MappingRepository;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ApiListEntity;
import com.sk.skala.axcalibur.global.entity.MappingEntity;
import com.sk.skala.axcalibur.global.entity.ParameterEntity;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.entity.TestCaseDataEntity;
import com.sk.skala.axcalibur.global.entity.TestCaseEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.ParameterRepository;
import com.sk.skala.axcalibur.global.repository.TestCaseDataRepository;
import com.sk.skala.axcalibur.global.repository.TestCaseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 테스트케이스 관리 커맨드 서비스 구현체
 * 테스트케이스의 수정, 삭제, 추가 기능을 제공합니다.
 * - 테스트케이스와 관련된 프로젝트 인증을 수행한 뒤, 해당 리소스를 안전하게 수정 또는 삭제합니다.
 * - 테스트 데이터(value)의 수정을 처리합니다.
 * - 테스트케이스를 수동으로 추가합니다.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class TcCommandServiceImpl implements TcCommandService {
    private final ApiListRepository apiListRepository;
    private final MappingRepository mappingRepository;
    private final ParameterRepository parameterRepository;
    private final ScenarioRepository scenarioRepository;
    private final TestCaseRepository testCaseRepository;
    private final TestCaseDataRepository testCaseDataRepository;

    // TC 이름 생성을 위한 템플릿
    private static final String TESTCASE_PREFIX = "TC";
    private static final String TESTCASE_ID_TEMPLATE = "%s-%s-%s-%s"; // prefix-apiName-index-uuid
    private final TimeBasedGenerator uuid;
    
    // TC 삭제 구현
    @Override
    @Transactional
    public void deleteTestcase(String tcId, Integer projectId) {
        // 테스트케이스 조회
        TestCaseEntity tc = testCaseRepository.findWithProjectByTestcaseId(tcId)
            .orElseThrow(() -> new BusinessExceptionHandler("TC ID가 존재하지 않습니다.", ErrorCode.NOT_FOUND_ERROR));

        // 프로젝트 인증 확인
        validateOwnership(tc, projectId);

        // 연관 테스트데이터 삭제
        testCaseRepository.delete(tc);
    }

    // TC 수정 구현
    @Override
    @Transactional
    public void updateTestcase(String tcId, Integer projectId, TcUpdateRequest request) {
        // 테스트케이스 조회
        TestCaseEntity tc = testCaseRepository.findWithProjectByTestcaseId(tcId)
            .orElseThrow(() -> new BusinessExceptionHandler("TC ID가 존재하지 않습니다.", ErrorCode.NOT_FOUND_ERROR));

        // 프로젝트 인증 확인
        validateOwnership(tc, projectId);

        // 수정 대상 필드가 아무것도 전달되지 않은 경우 방어
        validateUpdateRequest(request);
        
        // TC 정보 수정
        tc.update(request.getPrecondition(), request.getDescription(), request.getExpectedResult(), request.getStatus());

        // 테스트 데이터 수정
        if (request.getTestDataList() != null) {
            Map<Integer, String> paramMap = new HashMap<>();
            for (TcParamDto dto : request.getTestDataList()) {
                Integer key = dto.getParamId();
                if (key != null) {
                    paramMap.put(key, dto.getValue());  // value의 null 허용
                }
            }
            List<TestCaseDataEntity> dataList = testCaseDataRepository
                .findAllWithParameterByTestcaseId(tc.getId());

            for (TestCaseDataEntity data : dataList) {
                Integer paramId = data.getParameterKey().getId();
                if (paramMap.containsKey(paramId)) {
                    String value = paramMap.get(paramId);
                    data.updateValue(value);
                }
            }
        }
    }

    // TC 추가 구현
    @Override
    @Transactional
    public String addTestcase(String scenarioId, String apiId, TcUpdateRequest request) {
        // 추가를 위한 시나리오, API, 매핑표 조회
        ScenarioEntity scenario = scenarioRepository.findByScenarioId(scenarioId)
            .orElseThrow(() -> new BusinessExceptionHandler("유효하지 않은 시나리오 ID입니다.", ErrorCode.NOT_FOUND_ERROR));

        ApiListEntity api = apiListRepository.findByApiListId(apiId)
            .orElseThrow(() -> new BusinessExceptionHandler("유효하지 않은 API ID입니다.", ErrorCode.NOT_FOUND_ERROR));

        MappingEntity mapping = mappingRepository.findByScenarioKey_IdAndApiListKey_Id(scenario.getId(), api.getId())
            .orElseThrow(() -> new BusinessExceptionHandler("해당 시나리오-API 매핑이 존재하지 않습니다.", ErrorCode.NOT_FOUND_ERROR));

        // 테스트케이스 ID 생성
        String tcId = generateTestcaseId(api.getName());

        // 테스트케이스 저장
        TestCaseEntity testCase = TestCaseEntity.builder()
            .testcaseId(tcId)
            .precondition(request.getPrecondition())
            .description(request.getDescription())
            .expected(request.getExpectedResult())
            .status(request.getStatus())
            .mappingKey(mapping)
            .build();

        testCaseRepository.save(testCase);

        // 4. 필요한 파라미터 ID만 추출하여 한 번에 조회
        Map<Integer, ParameterEntity> paramMap = parameterRepository.findByIdIn(
            request.getTestDataList().stream()
                .map(TcParamDto::getParamId)
                .toList()
        ).stream().collect(Collectors.toMap(ParameterEntity::getId, p -> p));

        // 5. TestCaseDataEntity 생성
        List<TestCaseDataEntity> dataList = request.getTestDataList().stream()
            .map(dto -> {
                ParameterEntity param = paramMap.get(dto.getParamId());
                if (param == null) {
                    throw new BusinessExceptionHandler("유효하지 않은 파라미터 ID입니다.", ErrorCode.NOT_FOUND_ERROR);
                }
                return TestCaseDataEntity.builder()
                    .testcaseKey(testCase)
                    .parameterKey(param)
                    .value(dto.getValue())
                    .build();
            }).toList();
        
        testCaseDataRepository.saveAll(dataList);

        return tcId;
    }

    // 프로젝트 인증 확인 함수(TC는 있지만 프로젝트 ID와 안 맞는 경우 → 인가(authorization) 실패)
    private void validateOwnership(TestCaseEntity tc, Integer projectId) {
        if (!tc.getMappingKey().getScenarioKey().getProject().getId().equals(projectId)) {
            throw new BusinessExceptionHandler("해당 프로젝트에 접근 권한이 없습니다.", ErrorCode.FORBIDDEN_ERROR);
        }
    }
    
    // 프로젝트 수정 가능 확인 함수
    private void validateUpdateRequest(TcUpdateRequest request) {
        if (request.getDescription() == null && 
            request.getExpectedResult() == null &&
            request.getPrecondition() == null &&
            request.getStatus() == null &&
            (request.getTestDataList() == null || request.getTestDataList().isEmpty())) {
            throw new BusinessExceptionHandler("수정할 항목이 없습니다.", ErrorCode.BAD_REQUEST_ERROR);
        }
    }

    // TC ID 생성 함수(ex. TC-UserCreate-001-a1b2c3d4e5)
    private String generateTestcaseId(String apiName) {
        String prefixWithName = String.join("-", TESTCASE_PREFIX, apiName); // ex. TC-UserCreate
        int count = testCaseRepository.countByTestcaseIdStartingWith(prefixWithName); // TC-UserCreate 로 시작하는 TC ID 수 조회(일련번호 생성용)

        String index = String.format("%03d", count + 1);
        String id = uuid.generate().toString().substring(0, 10);

        return String.format(TESTCASE_ID_TEMPLATE, TESTCASE_PREFIX, apiName, index, id);
    }
}
