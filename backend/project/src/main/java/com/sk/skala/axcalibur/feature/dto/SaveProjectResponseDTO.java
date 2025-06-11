package com.sk.skala.axcalibur.feature.dto;

import java.time.LocalDateTime;

// 프로젝트 저장 응답 DTO (IF-PR-0001)
// 설계서 기준: requestTime 응답
public class SaveProjectResponseDTO {
    
    private LocalDateTime requestTime;   // 요청 생성 시간
    
    // 기본 생성자
    public SaveProjectResponseDTO() {}
    
    // 생성자
    public SaveProjectResponseDTO(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }
    
    // getter
    public LocalDateTime getRequestTime() {
        return requestTime;
    }
    
    // setter
    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }
} 