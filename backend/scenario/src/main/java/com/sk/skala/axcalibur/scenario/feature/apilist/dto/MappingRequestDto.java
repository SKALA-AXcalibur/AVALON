package com.sk.skala.axcalibur.scenario.feature.apilist.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class MappingRequestDto {
    private List<ScenarioDto> scenarioList;
    private List<ApiDto> apiList;

    public MappingRequestDto(List<ScenarioDto> scenarioList, List<ApiDto> apiList) {
        this.scenarioList = scenarioList;
        this.apiList = apiList;
    }
}
