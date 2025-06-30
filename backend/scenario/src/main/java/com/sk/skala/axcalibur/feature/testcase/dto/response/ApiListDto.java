package com.sk.skala.axcalibur.feature.testcase.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API 목록 DTO
 * TC를 추가할 시나리오에 대한 API 정보(ID, 이름) 포함된 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiListDto {
    private String apiId;
    private String apiName;
}
