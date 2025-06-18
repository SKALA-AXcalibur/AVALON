package com.sk.skala.axcalibur.feature.testcase.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestcaseGenerationResponse {
    private String processedAt;             // 처리 완료 시간
    private double validationRate;          // 검증률
    private String tcId;                    // 테스트케이스 ID
    private String precondition;            // 사전조건
    private String description;             // 테스트케이스 설명
    private String expectedResult;          // 예상 결과

    private List<ApiParamDto> testData;     // 테스트 데이터 목록 (parameter 구조 동일)
}