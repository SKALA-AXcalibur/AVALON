package com.sk.skala.axcalibur.spec.feature.apilist.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class MappingRequestDto {
    private List<ScenarioDto> scenarioList;
    private List<ApiDto> apiList;
}
