package com.sk.skala.axcalibur.spec.feature.report.service;

import com.sk.skala.axcalibur.spec.feature.report.dto.BusinessFunctionResult;
import com.sk.skala.axcalibur.spec.feature.report.dto.response.ReportResponseDto;
import com.sk.skala.axcalibur.spec.feature.report.entity.MappingEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.ScenarioEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseDataEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestcaseResultEntity;
import com.sk.skala.axcalibur.spec.feature.report.repository.MappingRepository;
import com.sk.skala.axcalibur.spec.feature.report.repository.ScenarioRepository;
import com.sk.skala.axcalibur.spec.feature.report.repository.TestCaseDataRepository;
import com.sk.skala.axcalibur.spec.feature.report.repository.TestCaseRepository;
import com.sk.skala.axcalibur.spec.feature.report.repository.TestcaseResultRepository;
import com.sk.skala.axcalibur.spec.global.code.ErrorCode;
import com.sk.skala.axcalibur.spec.global.entity.AvalonCookieEntity;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.spec.global.repository.AvalonCookieRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * 리포트 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ScenarioRepository scenarioRepository;
    private final MappingRepository mappingRepository;
    private final TestCaseRepository testCaseRepository;
    private final TestCaseDataRepository testCaseDataRepository;
    private final AvalonCookieRepository avalonCookieRepository;
    private final TestcaseResultRepository testcaseResultRepository;
    private final FileTemplateService fileTemplateService;

    /**
     * 테스트시나리오 리포트 다운로드
     */
    public ReportResponseDto downloadTestScenarioReport(String avalon) {
        // 1. 토큰 검증
        AvalonCookieEntity userInfo = validateAvalonToken(avalon);

        // 2. 시나리오 데이터 조회
        List<ScenarioEntity> scenarios = scenarioRepository.findByProjectKey_Key(userInfo.getProjectKey());
        log.info("사용자 시나리오 조회 완료: {}개", scenarios.size());

        // 3. 업무기능 계산
        String businessFunction = calculateBusinessFunctionFromScenarios(scenarios);

        // 4. 엑셀 리포트 생성
        byte[] fileData = fileTemplateService.generateScenarioReport(scenarios, businessFunction);
        
        // 5. 응답 반환
        return ReportResponseDto.builder()
                .avalon(avalon)
                .fileName("scenario_report.xlsx")
                .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .fileData(fileData)
                .build();
    }

    /**
     * 테스트케이스 리포트 다운로드
     */
    public ReportResponseDto downloadTestCaseReport(String scenarioId, String avalon) {
        // 1. 토큰 검증
        validateAvalonToken(avalon);

        // 2. 시나리오 조회
        ScenarioEntity scenario = getScenario(scenarioId);
        
        // 3. 테스트케이스 데이터 조회
        List<MappingEntity> mappings = getMappingsByScenarioId(scenarioId);
        List<TestCaseEntity> testCases = getTestCasesByMappings(mappings);
        List<TestCaseDataEntity> testCaseData = getTestCaseDataByTestCases(testCases);
        
        log.info("시나리오 ID: {} 에 대한 테스트케이스 데이터 조회 완료 - 매핑: {}개, 테스트케이스: {}개, 데이터: {}개", 
                scenarioId, mappings.size(), testCases.size(), testCaseData.size());

        // 4. 테스트케이스 결과 조회
        List<Integer> testCaseIds = extractTestCaseIds(testCases);
        List<TestcaseResultEntity> testCaseResults = testcaseResultRepository.findAllByTestcase_IdInWithFetch(testCaseIds)
            .stream()
            .sorted(Comparator.comparing(TestcaseResultEntity::getCreateAt).reversed())
            .collect(Collectors.toList());

        // 5. 업무기능 계산
        String businessFunction = getMostUsedBusinessFunction(testCases);
        
        // 6. 엑셀 리포트 생성
        byte[] fileData = fileTemplateService.generateTestCaseReport(testCases, testCaseData, testCaseResults, businessFunction);
        
        // 7. 응답 반환
        return ReportResponseDto.builder()
                .avalon(avalon)
                .fileName("testcase_report_" + scenarioId + ".xlsx")
                .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .fileData(fileData)
                .build();
    }

    /**
     * 토큰 검증 및 사용자 정보 반환
     */
    private AvalonCookieEntity validateAvalonToken(String avalon) {
        return avalonCookieRepository.findByToken(avalon)
                .orElseThrow(() -> BusinessExceptionHandler.builder()
                        .errorCode(ErrorCode.NOT_VALID_COOKIE_ERROR)
                        .message("토큰이 유효하지 않습니다.")
                        .build());
    }

    /**
     * 시나리오 조회
     */
    private ScenarioEntity getScenario(String scenarioId) {
        return scenarioRepository.findByScenarioId(scenarioId)
                .orElseThrow(() -> BusinessExceptionHandler.builder()
                        .errorCode(ErrorCode.NOT_FOUND_ERROR)
                        .message("시나리오를 찾을 수 없습니다.")
                        .build());
    }

    /**
     * 시나리오별 매핑 조회
     */
    private List<MappingEntity> getMappingsByScenarioId(String scenarioId) {
        return mappingRepository.findByScenarioKey_ScenarioId(scenarioId);
    }

    /**
     * 매핑별 테스트케이스 조회
     */
    private List<TestCaseEntity> getTestCasesByMappings(List<MappingEntity> mappings) {
        if (mappings.isEmpty()) {
            return List.of();
        }
        return testCaseRepository.findByMappingKeyIn(mappings);
    }

    /**
     * 테스트케이스별 데이터 조회
     */
    private List<TestCaseDataEntity> getTestCaseDataByTestCases(List<TestCaseEntity> testCases) {
        if (testCases.isEmpty()) {
            return List.of();
        }
        
        List<Integer> testCaseIds = extractTestCaseIds(testCases);
        return testCaseDataRepository.findByTestcaseKey_IdInWithFetch(testCaseIds);
    }

    private String calculateBusinessFunctionFromScenarios(List<ScenarioEntity> scenarios) {
        if (scenarios.isEmpty()) {
            throw BusinessExceptionHandler.builder()
                .errorCode(ErrorCode.NOT_FOUND_ERROR)
                .message("시나리오 데이터가 없습니다.")
                .build();
        }
        
        Integer projectKey = scenarios.get(0).getProjectKey().getKey();
        List<BusinessFunctionResult> result = testCaseRepository.findMostUsedBusinessFunctionByProjectKey(projectKey);
        
        if (result.isEmpty()) {
            throw BusinessExceptionHandler.builder()
                .errorCode(ErrorCode.NOT_FOUND_ERROR)
                .message("업무기능 정보를 찾을 수 없습니다.")
                .build();
        }
        
        return result.get(0).getName();
    }

    private String getMostUsedBusinessFunction(List<TestCaseEntity> testCases) {
        if (testCases.isEmpty()) {
            throw BusinessExceptionHandler.builder()
                .errorCode(ErrorCode.NOT_FOUND_ERROR)
                .message("테스트케이스 데이터가 없습니다.")
                .build();
        }
        
        List<BusinessFunctionResult> result = testCaseRepository.findMostUsedBusinessFunction(extractTestCaseIds(testCases));
        
        if (result.isEmpty()) {
            throw BusinessExceptionHandler.builder()
                .errorCode(ErrorCode.NOT_FOUND_ERROR)
                .message("업무기능 정보를 찾을 수 없습니다.")
                .build();
        }
        
        return result.get(0).getName();
    }

    private List<Integer> extractTestCaseIds(List<TestCaseEntity> testCases) {
        return testCases.stream()
            .map(TestCaseEntity::getId)
            .collect(Collectors.toList());
    }
}