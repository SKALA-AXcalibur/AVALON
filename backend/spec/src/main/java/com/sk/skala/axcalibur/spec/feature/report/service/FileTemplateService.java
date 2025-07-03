package com.sk.skala.axcalibur.spec.feature.report.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    public byte[] generateScenarioReport() {
        ClassPathResource resource = new ClassPathResource(SCENARIO_TEMPLATE);

        if (!resource.exists()) {
            throw BusinessExceptionHandler.builder()
                .errorCode(ErrorCode.NOT_FOUND_ERROR)
                .message("시나리오 템플릿 파일을 찾을 수 없습니다.")
                .build();
        }

        XSSFWorkbook workbook = new XSSFWorkbook(resource.getInputStream());

        // 데이터 매핑 로직(구현 예정)

        // 바이트 배열 로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    /**
     * 테스트케이스 템플릿 처리
     */
    public byte[] generateTestCaseReport() {
        ClassPathResource resource = new ClassPathResource(TESTCASE_TEMPLATE);

        if (!resource.exists()) {
            throw BusinessExceptionHandler.builder()
                .errorCode(ErrorCode.NOT_FOUND_ERROR)
                .message("테스트케이스 템플릿 파일을 찾을 수 없습니다.")
                .build();
        }

        XSSFWorkbook workbook = new XSSFWorkbook(resource.getInputStream());

        // 데이터 매핑 로직(구현 예정)

        // 바이트 배열 로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}