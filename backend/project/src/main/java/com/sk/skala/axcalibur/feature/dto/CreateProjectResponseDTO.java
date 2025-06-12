package com.sk.skala.axcalibur.feature.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 프로젝트 생성 응답 DTO (IF-PR-0004)
 */
@Getter
@Setter
public class CreateProjectResponseDTO {

    private String projectId;
    private String avalon;

    public CreateProjectResponseDTO(String projectId, String avalon) {
        this.projectId = projectId;
        this.avalon = avalon;
    }
} 