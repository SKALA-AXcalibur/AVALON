package com.sk.skala.axcalibur.feature.scenario.dto.request;

import java.util.List;

import com.sk.skala.axcalibur.feature.scenario.dto.request.item.MappingApiItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.MappingScenarioItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MappingRequestDto {
    private List<MappingScenarioItem> scenarioList; // 시나리오 목록
    private List<MappingApiItem> apiList;           // API 목록

    public MappingRequestDto(List<MappingScenarioItem> scenarioList, List<MappingApiItem> apiList) {
        this.scenarioList = scenarioList;
        this.apiList = apiList;
    }
}
