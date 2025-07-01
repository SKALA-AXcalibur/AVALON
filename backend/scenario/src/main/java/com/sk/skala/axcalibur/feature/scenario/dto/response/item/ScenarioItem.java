package com.sk.skala.axcalibur.feature.scenario.dto.response.item;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioItem {
   
    private String scenarioId; // 시나리오 아이디
    private String title; // 시나리오 이름
    private String description; // 시나리오 설명
    private String validation; // 검증 포인트
    private List<ApiItem> apiList; // API 목록
}
