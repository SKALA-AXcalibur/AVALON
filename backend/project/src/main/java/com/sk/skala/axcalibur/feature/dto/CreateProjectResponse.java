package com.sk.skala.axcalibur.feature.dto;

// import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 프로젝트 생성 응답 DTO (IF-PR-0004)
 */
public class CreateProjectResponse {

    private String projectId;
    private String avalon;

    public CreateProjectResponse(String projectId, String avalon) {
        this.projectId = projectId;
        this.avalon = avalon;
    }

    //<editor-fold desc="Getter">
    public String getProjectId() {
        return projectId;
    }

    public String getAvalon() {
        return avalon;
    }
    //</editor-fold>
} 