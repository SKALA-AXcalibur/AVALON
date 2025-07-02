package com.sk.skala.axcalibur.feature.apilist.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class MappingRequestDto {
    private List<ScenarioDto> scenarioList; // 시나리오 목록
    private List<ApiDto> apiList;           // API 목록

    public MappingRequestDto(List<ScenarioDto> scenarioList, List<ApiDto> apiList) {
        this.scenarioList = scenarioList;
        this.apiList = apiList;
    }
}
