package com.sk.skala.axcalibur.feature.project.dto;

import lombok.Getter;

/**
 * 프로젝트 생성 응답 DTO (IF-PR-0004)
 */
@Getter
public class CreateProjectResponseDto {

    private String projectId;
    private String avalon;

    public CreateProjectResponseDto(String projectId, String avalon) {
        this.projectId = projectId;
        this.avalon = avalon;
    }
} 