package com.sk.skala.axcalibur.feature.testcase.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TC 전체 데이터 DTO
 * 생성된 TC에 대한 개별 정보
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestcaseDataDto {
    private Integer mappingId;                  // 매핑표 ID
    private String tcId;                        // 테스트케이스 ID
    private String precondition;                // 사전조건
    private String description;                 // 테스트케이스 설명
    private String expectedResult;              // 예상 결과

    private List<TestcaseParamDto> testDataList;    // 테스트 데이터 목록 (parameter 구조 + 예상값)
}
