package com.sk.skala.axcalibur.spec.feature.project.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ApiMappingRequestDto {
    private String prcessedAt;
    
    public ApiMappingRequestDto() {
        this.prcessedAt = LocalDateTime.now().toString();
    }

}
