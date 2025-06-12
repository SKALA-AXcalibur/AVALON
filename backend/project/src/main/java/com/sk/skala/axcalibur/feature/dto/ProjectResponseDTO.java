package com.sk.skala.axcalibur.feature.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// 프로젝트 조회 응답 DTO (IF-PR-0002)
@Getter
@Setter
public class ProjectResponseDTO {
    
    private String projectId;
    private String projectName;
    private List<String> specList;
    private List<RequirementInfoDTO> requirement;
    private List<ApiInfoDTO> apiList;

    // 이 필드는 컨트롤러에서 쿠키 생성을 위해 필요하지만, JSON 응답에는 포함되지 않아야 함
    @JsonIgnore
    private String avalon;


    public ProjectResponseDTO(String projectId, String avalon, String projectName, List<String> specList, List<RequirementInfoDTO> requirement, List<ApiInfoDTO> apiList) {
        this.projectId = projectId;
        this.avalon = avalon;
        this.projectName = projectName;
        this.specList = specList;
        this.requirement = requirement;
        this.apiList = apiList;
    }
}