package com.sk.skala.axcalibur.feature.project.dto;

import java.time.LocalDateTime;

import lombok.Getter;

// 프로젝트 삭제 응답 DTO (IF-PR-0003)

@Getter
public class DeleteProjectResponseDto {
    private String requestTime;
    
    public DeleteProjectResponseDto() {
        this.requestTime = LocalDateTime.now().toString();
    }

}