package com.sk.skala.axcalibur.spec.feature.report.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestScenarioReportResponseDto {
    private String avalon;              // 토큰
    private byte[] fileData;            // 엑셀 파일 바이너리 데이터
    private String fileName;            // 파일명
    private String contentType;         // 파일 타입
}