package com.sk.skala.axcalibur.spec.feature.report.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sk.skala.axcalibur.spec.feature.report.entity.ScenarioEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseDataEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestcaseResultEntity;
import com.sk.skala.axcalibur.spec.global.code.ErrorCode;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 파일 템플릿 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileTemplateServiceImpl implements FileTemplateService {

    @Value("${template.scenario-file}")
    private String scenarioTemplate;

    @Value("${template.testcase-file}")
    private String testcaseTemplate;
    
    /**
     * 시나리오 템플릿 처리
     */
    public byte[] generateScenarioReport(List<ScenarioEntity> scenarios, String businessFunction) {
        ClassPathResource resource = new ClassPathResource(scenarioTemplate);

        if (!resource.exists()) {
            throw BusinessExceptionHandler.builder()
                .errorCode(ErrorCode.NOT_FOUND_ERROR)
                .message("시나리오 템플릿 파일을 찾을 수 없습니다.")
                .build();
        }
        
        try (XSSFWorkbook workbook = new XSSFWorkbook(resource.getInputStream());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.getSheetAt(2);
            
            setCellValue(sheet, 1, 1, businessFunction);
            
            // 개선된 동적 매핑 로직
            mapScenarioDataToExcel(sheet, scenarios);

            // 바이트 배열로 변환
            workbook.write(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw BusinessExceptionHandler.builder()
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
                .message("엑셀 파일 생성 실패")
                .build();
        }
    }

    /**
     * 테스트케이스 템플릿 처리
     */
    public byte[] generateTestCaseReport(List<TestCaseEntity> testCases, List<TestCaseDataEntity> testCaseData, List<TestcaseResultEntity> testCaseResults, String businessFunction) {
        ClassPathResource resource = new ClassPathResource(testcaseTemplate);

        if (!resource.exists()) {
            throw BusinessExceptionHandler.builder()
                .errorCode(ErrorCode.NOT_FOUND_ERROR)
                .message("테스트케이스 템플릿 파일을 찾을 수 없습니다.")
                .build();
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook(resource.getInputStream());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.getSheetAt(2);
            
            setCellValue(sheet, 1, 1, businessFunction);

            int testCount = testCaseResults.size();
            setCellValue(sheet, 1, 3, testCount);
            setCellValue(sheet, 2, 1, "AVALON");
            setCellValue(sheet, 2, 3, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            if (!testCases.isEmpty()) {
                String scenarioId = testCases.get(0).getMappingKey().getScenarioKey().getScenarioId();
                setCellValue(sheet, 5, 1, scenarioId);
            }

            mapTestCaseDataToExcel(sheet, testCases, testCaseData, testCaseResults);

            // 바이트 배열로 변환
            workbook.write(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw BusinessExceptionHandler.builder()
                .errorCode(ErrorCode.FILE_STORAGE_ERROR)
                .message("엑셀 파일 생성 중 오류 발생")
                .build();
        }
    }

    /**
     * 테스트 케이스 데이터 매핑
     */
    private void mapTestCaseDataToExcel(XSSFSheet sheet, List<TestCaseEntity> testCases, List<TestCaseDataEntity> testCaseData, List<TestcaseResultEntity> testCaseResults) {

        // N+1 문제 해결: ID를 미리 추출하여 맵 생성
        Map<Integer, List<TestCaseDataEntity>> testCaseDataMap = testCaseData.stream()
                .collect(Collectors.groupingBy(data -> {
                    TestCaseEntity testCase = data.getTestcaseKey();
                    return testCase != null ? testCase.getId() : null;
                }));
        
        Map<Integer, List<TestcaseResultEntity>> testCaseResultMap = testCaseResults.stream()
                .collect(Collectors.groupingBy(result -> {
                    TestCaseEntity testCase = result.getTestcase();
                    return testCase != null ? testCase.getId() : null;
                }));
    
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
                
                // 이탤릭 해제 - 새로운 스타일 생성하여 적용
                XSSFRow row = sheet.getRow(dataRow);
                IntStream.range(0, 4).forEach(col -> {
                    XSSFCell cell = row.getCell(col);
                    if (cell != null) {
                        // 기존 스타일을 복제한 새로운 스타일 생성
                        XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
                        if (cell.getCellStyle() != null) {
                            style.cloneStyleFrom(cell.getCellStyle());
                        }
                        
                        // 새로운 Font 생성하여 이탤릭 해제
                        XSSFFont font = sheet.getWorkbook().createFont();
                        font.setItalic(false);
                        style.setFont(font);
                        
                        // 새로운 스타일 적용
                        cell.setCellStyle(style);
                    }
                });
            });
        log.info("시나리오 템플릿 매핑 완료, 시나리오: {}개", scenarios.size());
    }

    /**
     * 안전한 셀 값 설정
     */
    private void setCellValue(XSSFSheet sheet, int rowIndex, int columnIndex, Object value) {
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
        
        // Font 스타일 설정 (Apache POI 5.x 호환)
        if (cell.getCellStyle() == null) {
            cell.setCellStyle(sheet.getWorkbook().createCellStyle());
        }
        
        // 새로운 Font 생성하여 이탤릭 해제
        XSSFFont font = sheet.getWorkbook().createFont();
        font.setItalic(false);
        cell.getCellStyle().setFont(font);
    }
}