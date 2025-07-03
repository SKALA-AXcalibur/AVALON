package com.sk.skala.axcalibur.feature.testcase.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API 목록 반환 응답 객체
 * TC를 추가할 시나리오에 대한 API 정보(ApiListDto) list 반환
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiListResponse {
    private List<ApiListDto> apiList;
}
