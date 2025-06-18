package com.sk.skala.axcalibur.scenario.feature.apilist.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ApiMappingRequestDto {
    private String processedAt;
    
    public ApiMappingRequestDto() {
        this.processedAt = LocalDateTime.now().toString();
    }

}
