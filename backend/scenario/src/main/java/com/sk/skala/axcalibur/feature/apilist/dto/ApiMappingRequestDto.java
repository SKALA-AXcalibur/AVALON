package com.sk.skala.axcalibur.feature.apilist.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ApiMappingRequestDto {
    private String processedAt; // 처리 시간
    
    public ApiMappingRequestDto() {
        this.processedAt = LocalDateTime.now().toString(); // 현재 시간을 문자열로 변환하여 저장
    }

}
