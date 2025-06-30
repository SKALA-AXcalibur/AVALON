package com.sk.skala.axcalibur.feature.testcase.dto.response;

import java.util.List;

import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API 선택 DTO
 * TC를 추가할 시나리오 중 선택된 API에 대한 파라미터 목록 조회 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiParamListResponse {
    private List<ApiParamDto> testDataList;
}
