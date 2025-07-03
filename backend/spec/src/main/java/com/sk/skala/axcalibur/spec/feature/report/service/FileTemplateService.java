package com.sk.skala.axcalibur.spec.feature.report.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sk.skala.axcalibur.spec.feature.report.entity.MappingEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.ScenarioEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseDataEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseEntity;
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
    public byte[] generateTestCaseReport(List<TestCaseEntity> testCases, List<TestCaseDataEntity> testCaseData) throws IOException {
        ClassPathResource resource = new ClassPathResource(TESTCASE_TEMPLATE);

        if (!resource.exists()) {
            throw BusinessExceptionHandler.builder()
                .errorCode(ErrorCode.NOT_FOUND_ERROR)
                .message("테스트케이스 템플릿 파일을 찾을 수 없습니다.")
                .build();
        }

        XSSFWorkbook workbook = new XSSFWorkbook(resource.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(2);

        mapTestCaseDataToExcel(sheet, testCases, testCaseData);

        // 바이트 배열로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    /**
     * 테스트 케이스 데이터 매핑
     */
    private void mapTestCaseDataToExcel(XSSFSheet sheet, List<TestCaseEntity> testCases, List<TestCaseDataEntity> testCaseData) {

        Map<Integer, List<TestCaseDataEntity>> testCaseDataMap = testCaseData.stream()
                .collect(Collectors.groupingBy(data -> data.getTestcaseKey().getId()));

        IntStream.range(0, testCases.size())
            .forEach(i -> {
                TestCaseEntity testCase = testCases.get(i);
                List<TestCaseDataEntity> dataList = testCaseDataMap.get(testCase.getId());
                int dataRow = 8 + i; // 9행부터 데이터 시작 (8행이 헤더)
                
                // 실제 템플릿 컬럼에 맞춘 매핑
                setCellValue(sheet, dataRow, 0, testCase.getTestcaseId());      // A열: 테스트 케이스 ID
                setCellValue(sheet, dataRow, 1, testCase.getDescription());     // B열: 테스트 케이스(절차)
                setCellValue(sheet, dataRow, 2, testCase.getPrecondition());    // C열: 사전조건
                setCellValue(sheet, dataRow, 4, testCase.getExpected());        // E열: 예상결과
                
                // D열: 테스트 데이터 (TestCaseDataEntity의 value들을 연결)
                if (dataList != null && !dataList.isEmpty()) {
                    String testDataValues = dataList.stream()
                            .map(TestCaseDataEntity::getValue)
                            .filter(value -> value != null && !value.trim().isEmpty())
                            .collect(Collectors.joining(", "));
                    setCellValue(sheet, dataRow, 3, testDataValues);
                }
                
                // F, G, H열은 빈 값으로 두기 (테스트 실행 후 수동 입력)
                setCellValue(sheet, dataRow, 5, "");  // F열: 수행결과
                setCellValue(sheet, dataRow, 6, "");  // G열: PASS/FAIL  
                setCellValue(sheet, dataRow, 7, "");  // H열: 비고
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
                setCellValue(sheet, dataRow, 3, "");
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
}