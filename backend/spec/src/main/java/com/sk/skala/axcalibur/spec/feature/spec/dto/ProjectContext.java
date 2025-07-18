package com.sk.skala.axcalibur.spec.feature.spec.dto;

import com.sk.skala.axcalibur.spec.global.entity.ProjectEntity;

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
        this.key = entity.getKey();
        this.projectId = entity.getId();
    }
}
