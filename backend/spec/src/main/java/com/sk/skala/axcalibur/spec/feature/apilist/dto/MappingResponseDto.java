package com.sk.skala.axcalibur.spec.feature.apilist.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;

@Getter
public class MappingResponseDto {
    private String processedAt;
    private Double validationRate;
    private List<ApiMappingDto> apiMappingList;

    public MappingResponseDto(String processedAt, Double validationRate, List<ApiMappingDto> apiMappingList) {
        this.processedAt = processedAt;
        this.validationRate = validationRate;
        this.apiMappingList = apiMappingList;
    }
}