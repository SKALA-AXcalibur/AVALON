package com.sk.skala.axcalibur.spec.feature.report.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sk.skala.axcalibur.spec.feature.report.entity.MappingEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.ScenarioEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseDataEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestcaseResultEntity;
import com.sk.skala.axcalibur.spec.feature.report.repository.MappingRepository;
import com.sk.skala.axcalibur.spec.feature.report.repository.TestCaseRepository;
import com.sk.skala.axcalibur.spec.global.code.ErrorCode;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileTemplateService {

    private static final String SCENARIO_TEMPLATE = "AXcalibur_500. 서비스_분석 설계서_512_통합테스트시나리오_v0.1.xlsx";
    private static final String TESTCASE_TEMPLATE = "AXcalibur_500. 서비스_분석 설계서_513_통합테스트케이스(결과)_v0.1.xlsx";

    private final MappingRepository mappingRepository;
    private final TestCaseRepository testCaseRepository;

    /**
     * 시나리오 템플릿 처리
     */
    public byte[] generateScenarioReport(List<ScenarioEntity> scenarios) throws IOException {
        ClassPathResource resource = new ClassPathResource(SCENARIO_TEMPLATE);

        if (!resource.exists()) {
            throw BusinessExceptionHandler.builder()
                .errorCode(ErrorCode.NOT_FOUND_ERROR)
                .message("시나리오 템플릿 파일을 찾을 수 없습니다.")
                .build();
        }

        XSSFWorkbook workbook = new XSSFWorkbook(resource.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(2);

        // 시나리오들의 테스트케이스들을 조회해서 업무기능 결정
        if (!scenarios.isEmpty()) {
            // 시나리오들에 속한 모든 테스트케이스 조회
            List<TestCaseEntity> allTestCases = new ArrayList<>();
            for (ScenarioEntity scenario : scenarios) {
                List<MappingEntity> mappings = mappingRepository.findByScenarioKey_ScenarioId(scenario.getScenarioId());
                List<TestCaseEntity> testCases = testCaseRepository.findByMappingKeyIn(mappings);
                allTestCases.addAll(testCases);
            }
            
            // 가장 많이 사용된 업무기능 조회
            String businessFunction = getMostUsedBusinessFunction(allTestCases);
            setCellValue(sheet, 1, 1, businessFunction);  // B2 셀
        }

        // 개선된 동적 매핑 로직
        mapScenarioDataToExcel(sheet, scenarios);

        // 바이트 배열로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    /**
     * 테스트케이스 템플릿 처리
     */
    public byte[] generateTestCaseReport(List<TestCaseEntity> testCases, List<TestCaseDataEntity> testCaseData, List<TestcaseResultEntity> testCaseResults) throws IOException {
        ClassPathResource resource = new ClassPathResource(TESTCASE_TEMPLATE);

        if (!resource.exists()) {
            throw BusinessExceptionHandler.builder()
                .errorCode(ErrorCode.NOT_FOUND_ERROR)
                .message("테스트케이스 템플릿 파일을 찾을 수 없습니다.")
                .build();
        }

        XSSFWorkbook workbook = new XSSFWorkbook(resource.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(2);

        String businessFunction = getMostUsedBusinessFunction(testCases);
        setCellValue(sheet, 1, 1, businessFunction);

        int testCount = testCaseResults.size();
        setCellValue(sheet, 1, 3, testCount);

        setCellValue(sheet, 2, 1, "AVALON");

        if (!testCases.isEmpty()) {
            String scenarioId = testCases.get(0).getMappingKey().getScenarioKey().getScenarioId();
            setCellValue(sheet, 5, 1, scenarioId);
        }

        mapTestCaseDataToExcel(sheet, testCases, testCaseData, testCaseResults);

        // 바이트 배열로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    /**
     * 테스트 케이스 데이터 매핑
     */
    private void mapTestCaseDataToExcel(XSSFSheet sheet, List<TestCaseEntity> testCases, List<TestCaseDataEntity> testCaseData, List<TestcaseResultEntity> testCaseResults) {

        Map<Integer, List<TestCaseDataEntity>> testCaseDataMap = testCaseData.stream()
                .collect(Collectors.groupingBy(data -> data.getTestcaseKey().getId()));
        
        // TestcaseResultEntity 매핑 추가
        Map<Integer, List<TestcaseResultEntity>> testCaseResultMap = testCaseResults.stream()
                .collect(Collectors.groupingBy(result -> (Integer) result.getTestcase().getId()));
    
        IntStream.range(0, testCases.size())
            .forEach(i -> {
                TestCaseEntity testCase = testCases.get(i);
                List<TestCaseDataEntity> dataList = testCaseDataMap.get(testCase.getId());
                List<TestcaseResultEntity> resultList = testCaseResultMap.get(testCase.getId());
                int dataRow = 8 + i;
                
                // 기존 매핑
                setCellValue(sheet, dataRow, 0, testCase.getTestcaseId());
                setCellValue(sheet, dataRow, 1, testCase.getDescription());
                setCellValue(sheet, dataRow, 2, testCase.getPrecondition());
                setCellValue(sheet, dataRow, 4, testCase.getExpected());
                
                if (dataList != null && !dataList.isEmpty()) {
                    String testDataValues = dataList.stream()
                            .map(TestCaseDataEntity::getValue)
                            .filter(value -> value != null && !value.trim().isEmpty())
                            .collect(Collectors.joining(", "));
                    setCellValue(sheet, dataRow, 3, testDataValues);
                }
                
                // TestcaseResultEntity에서 결과 매핑
                if (resultList != null && !resultList.isEmpty()) {
                    TestcaseResultEntity latestResult = resultList.get(0); // 첫 번째 결과 사용
                    setCellValue(sheet, dataRow, 5, latestResult.getResult());   // F열: 수행결과
                    setCellValue(sheet, dataRow, 6, latestResult.getSuccess() != null ? 
                        (latestResult.getSuccess() ? "PASS" : "FAIL") : "");      // G열: PASS/FAIL
                    setCellValue(sheet, dataRow, 7, latestResult.getReason());   // H열: 비고
                } else {
                    setCellValue(sheet, dataRow, 5, "");  
                    setCellValue(sheet, dataRow, 6, "");  
                    setCellValue(sheet, dataRow, 7, "");
                }
            });
        log.info("테스트케이스 템플릿 매핑 완료, 테스트케이스: {}개", testCases.size());
        
    }

    /**
     * 시나리오 데이터 매핑
     */
    private void mapScenarioDataToExcel(XSSFSheet sheet, List<ScenarioEntity> scenarios) {
        IntStream.range(0, scenarios.size())
            .forEach(i -> {
                ScenarioEntity scenario = scenarios.get(i);
                int dataRow = 4 + i;

                setCellValue(sheet, dataRow, 0, scenario.getScenarioId());
                setCellValue(sheet, dataRow, 1, scenario.getName());
                setCellValue(sheet, dataRow, 2, scenario.getDescription());
                setCellValue(sheet, dataRow, 3, scenario.getValidation());
            });
        log.info("시나리오 템플릿 매핑 완료, 시나리오: {}개", scenarios.size());
    }

    /**
     * 안전한 셀 값 설정
     */
    private void setCellValue(XSSFSheet sheet, int rowIndex, int columnIndex, Object value) {
        try {
            XSSFRow row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            
            XSSFCell cell = row.getCell(columnIndex);
            if (cell == null) {
                cell = row.createCell(columnIndex);
            }
            
            if (value instanceof String) {
                cell.setCellValue((String) value);
            } else if (value instanceof Integer) {
                cell.setCellValue((Integer) value);
            } else if (value instanceof Double) {
                cell.setCellValue((Double) value);
            } else if (value != null) {
                cell.setCellValue(value.toString());
            }
        } catch (Exception e) {
            log.warn("셀 값 설정 실패 - Row: {}, Column: {}, Value: {}", rowIndex, columnIndex, value, e);
        }
    }

    
    /**
     * 가장 많이 사용된 요구사항 대분류명 조회
     */
    private String getMostUsedBusinessFunction(List<TestCaseEntity> testCases) {
        Map<String, Long> majorCount = testCases.stream()
            .map(testCase -> testCase.getMappingKey().getApiListKey().getRequestKey().getMajorKey().getName())
            .collect(Collectors.groupingBy(
                majorName -> majorName,
                Collectors.counting()
            ));
        
        return majorCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("기본 업무");
    }
}