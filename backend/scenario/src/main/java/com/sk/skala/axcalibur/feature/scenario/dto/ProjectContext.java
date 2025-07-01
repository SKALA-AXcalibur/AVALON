package com.sk.skala.axcalibur.feature.scenario.dto;

import com.sk.skala.axcalibur.global.entity.ProjectEntity;

import lombok.Getter;

/**
 * 프로젝트 설정 dto
 * - 프로젝트 정보 (key, id) 저장
 */
@Getter
public class ProjectContext {
    private final Integer key;
    private final String projectId;

    public ProjectContext(ProjectEntity entity) {
        this.key = entity.getId();
        this.projectId = entity.getProjectId();
    }
}