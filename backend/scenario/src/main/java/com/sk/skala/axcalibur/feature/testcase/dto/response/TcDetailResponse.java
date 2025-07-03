package com.sk.skala.axcalibur.feature.testcase.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TC 데이터 조회 DTO
 * 특정 TC ID에 대한 TC 정보 반환
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TcDetailResponse {
    private String tcId;
    private String precondition;
    private String description;
    private String expectedResult;
    private Integer status;
    
    private List<TcParamDataDto> testDataList;
}
