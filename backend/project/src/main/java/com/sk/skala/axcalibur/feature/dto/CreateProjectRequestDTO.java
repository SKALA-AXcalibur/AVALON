package com.sk.skala.axcalibur.feature.dto;

// 프로젝트 생성 요청 DTO (IF-PR-0004)
// 설계서 기준: projectId 하나만 받음
public class CreateProjectRequestDTO {
    private String projectId;    // 프로젝트 ID
    
    // 기본 생성자
    public CreateProjectRequestDTO() {}
    
    // 생성자
    public CreateProjectRequestDTO(String projectId) {
        this.projectId = projectId;
    }
    
    // getter
    public String getProjectId() {
        return projectId;
    }
    
    // setter
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}