package com.sk.skala.axcalibur.feature.scenario.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sk.skala.axcalibur.feature.scenario.dto.response.item.ApiMappingResponseItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FastAPI 매핑 API 응답 DTO
 * FastAPI의 /api/list/v1/create 엔드포인트 실제 응답 구조와 매핑
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiMappingResponseDto {
    
    @JsonProperty("processedAt")
    private String processedAt;
    
    @JsonProperty("validationRate")
    private Double validationRate;
    
    @JsonProperty("apiMapping")
    private List<ApiMappingResponseItem> apiMapping;
} 