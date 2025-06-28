package com.sk.skala.axcalibur.feature.scenario.dto.request;

import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ScenarioFlowApi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 시나리오 흐름도 요청 DTO (하위)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioFlowDto {
    private String id;
    private String description;
    private List<ScenarioFlowApi> apiList;
    
}
