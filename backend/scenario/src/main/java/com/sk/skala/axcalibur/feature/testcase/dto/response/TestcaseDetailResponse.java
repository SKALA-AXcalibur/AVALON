package com.sk.skala.axcalibur.feature.testcase.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestcaseDetailResponse {
    private String tcId;
    private String precondition;
    private String description;
    private String expectedResult;
    private Integer status;
    
    private List<TestcaseParamDataDto> testDataList;
}
