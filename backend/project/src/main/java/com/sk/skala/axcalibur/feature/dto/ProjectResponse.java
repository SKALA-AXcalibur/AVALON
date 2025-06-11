package com.sk.skala.axcalibur.feature.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

// 프로젝트 조회 응답 DTO (IF-PR-0002)
public class ProjectResponse {
    
    private String projectId;
    private String projectName;
    private List<String> specList;
    private List<RequirementInfo> requirement;
    private List<ApiInfo> apiList;

    // 이 필드는 컨트롤러에서 쿠키 생성을 위해 필요하지만, JSON 응답에는 포함되지 않아야 함
    @JsonIgnore
    private String avalon;


    public ProjectResponse(String projectId, String avalon, String projectName, List<String> specList, List<RequirementInfo> requirement, List<ApiInfo> apiList) {
        this.projectId = projectId;
        this.avalon = avalon;
        this.projectName = projectName;
        this.specList = specList;
        this.requirement = requirement;
        this.apiList = apiList;
    }
    
    
    //<editor-fold desc="Getter and Setter">
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public List<String> getSpecList() {
        return specList;
    }

    public void setSpecList(List<String> specList) {
        this.specList = specList;
    }
    
    public List<RequirementInfo> getRequirement() {
        return requirement;
    }

    public void setRequirement(List<RequirementInfo> requirement) {
        this.requirement = requirement;
    }
    
    public List<ApiInfo> getApiList() {
        return apiList;
    }
    
    public void setApiList(List<ApiInfo> apiList) {
        this.apiList = apiList;
        }
        
    public String getAvalon() {
        return avalon;
        }
        
    public void setAvalon(String avalon) {
        this.avalon = avalon;
        }
    //</editor-fold>
}