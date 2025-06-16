package com.sk.skala.axcalibur.feature.project.dto;

import lombok.Getter;

// 프로젝트 생성 요청 DTO (IF-PR-0004)
// 설계서 기준: projectId 하나만 받음
@Getter
public class CreateProjectRequestDto {
    private String projectId;    // 프로젝트 ID
    
    // 기본 생성자
    public CreateProjectRequestDto() {}
    
    // 생성자
    public CreateProjectRequestDto(String projectId) {
        this.projectId = projectId;
    }
}