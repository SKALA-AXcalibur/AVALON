package com.sk.skala.axcalibur.feature.project.dto;

import java.time.LocalDateTime;

import lombok.Getter;

// 프로젝트 저장 응답 DTO (IF-PR-0001)
// 설계서 기준: requestTime 응답
@Getter
public class SaveProjectResponseDto {
    
    private LocalDateTime requestTime;   // 요청 생성 시간
    
    // 기본 생성자
    public SaveProjectResponseDto() {}
    
    // 생성자
    public SaveProjectResponseDto(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }
} 