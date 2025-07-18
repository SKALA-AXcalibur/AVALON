package com.sk.skala.axcalibur.spec.feature.project.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 프로젝트 조회 응답 DTO (IF-PR-0002)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDto {
    
    private String projectId;
    private String projectName;
    private List<RequirementInfoDto> requirement;
    private List<ApiInfoDto> apiList;
    private List<TableInfoDto> tableList;

    // 이 필드는 컨트롤러에서 쿠키 생성을 위해 필요하지만, JSON 응답에는 포함되지 않아야 함
    @JsonIgnore
    private String avalon;


    public ProjectResponseDto(String projectId, String avalon, String projectName, List<RequirementInfoDto> requirement, List<ApiInfoDto> apiList) {
        this.projectId = projectId;
        this.avalon = avalon;
        this.projectName = projectName;
        this.requirement = requirement;
        this.apiList = apiList;
    }
}