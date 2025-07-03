package com.sk.skala.axcalibur.feature.scenario.dto.request;

import java.util.List;

import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ApiItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ReqItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 시나리오 생성 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioGenRequestDto {
    
    private String projectId;
    private List<ReqItem> requirement;
    private List<ApiItem> apiList;
    
}