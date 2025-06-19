package com.sk.skala.axcalibur.feature.testcase.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;

/**
 * TC 데이터의 파라미터 정보 DTO
 * TC에 포함되는 파라미터 데이터에 대한 객체 정의
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestcaseParamDto {
    private ApiParamDto param;   // 파라미터 구조
    private String value;        // 테스트케이스 데이터에 들어갈 값
}