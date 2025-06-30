package com.sk.skala.axcalibur.scenario.feature.apilist.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;

@Getter
public class MappingResponseDto {
    private String processedAt;
    private Double validationRate;
    private List<ApiMappingDto> apiMapping;

    public MappingResponseDto(String processedAt, Double validationRate, List<ApiMappingDto> apiMapping) {
        this.processedAt = processedAt;
        this.validationRate = validationRate;
        this.apiMapping = apiMapping;
    }
}