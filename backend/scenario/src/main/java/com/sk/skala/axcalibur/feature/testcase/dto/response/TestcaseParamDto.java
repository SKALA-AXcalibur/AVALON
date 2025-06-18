package com.sk.skala.axcalibur.feature.testcase.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestcaseParamDto {
    private ApiParamDto param;   // 기존 구조
    private String value;        // 테스트케이스 데이터에 들어갈 값
}