package com.sk.skala.axcalibur.feature.testcase.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API 목록 DTO
 * TC를 추가할 시나리오에 대한 API 목록 정보 반환
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiListResponse {
    String apiId;
    String apiName;
}
