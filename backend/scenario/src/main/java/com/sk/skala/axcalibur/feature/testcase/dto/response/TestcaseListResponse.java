package com.sk.skala.axcalibur.feature.testcase.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TC 목록 조회 response DTO
 * 특정 시나리오에 매핑된 TC ID 목록을 응답으로 반환
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestcaseListResponse {
    Integer tcTotal;
    List<String> tcList;
}
