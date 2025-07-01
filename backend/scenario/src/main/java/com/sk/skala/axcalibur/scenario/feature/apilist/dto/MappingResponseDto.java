package com.sk.skala.axcalibur.scenario.feature.apilist.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class MappingResponseDto {
    private String processedAt;             // 처리 시간
    private Double validationRate;          // 검증 비율
    private List<ApiMappingDto> apiMapping; // API 매핑 목록

    public MappingResponseDto() {}

    public MappingResponseDto(String processedAt, Double validationRate, List<ApiMappingDto> apiMapping) {
        this.processedAt = processedAt;
        this.validationRate = validationRate;
        this.apiMapping = apiMapping;
    }
}