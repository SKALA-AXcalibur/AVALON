package com.sk.skala.axcalibur.feature.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


// 프로젝트 목록 저장 요청 DTO (IF-PR-0001)
// 설계서 기준: 상세한 요구사항 및 API 정보 포함
@Getter
@Setter
public class SaveProjectRequestDTO {
    
    private List<RequirementItem> requirement;
    private List<ApiItem> apiList;
    
    public SaveProjectRequestDTO() {}
    
    @Getter
    @Setter
    public static class RequirementItem {
        private String name;
        private String desc;
        private String priority;
        private String major;
        private String middle;
        private String minor;
        
        public RequirementItem() {}
        // getter/setter 메서드들 모두 삭제 가능
    }
    
    @Getter
    @Setter
    public static class ApiItem {
        private String id;
        private String name;
        private String desc;
        private String method;
        private String url;
        private String path;
        private String pathQuery;
        private ParameterGroup request;
        private ParameterGroup response;
        
        public ApiItem() {}
        // getter/setter 메서드들 모두 삭제 가능
    }
    
    @Getter
    @Setter
    public static class ParameterGroup {
        private List<ParameterItem> pq;
        private List<ParameterItem> req;
        private List<ParameterItem> res;
        
        public ParameterGroup() {}
        // getter/setter 메서드들 모두 삭제 가능
    }
    
    @Getter
    @Setter
    public static class ParameterItem {
        private String korName;
        private String name;
        private String itemType;
        private String step;
        private String dataType;
        private String length;
        private String format;
        private String defaultValue;
        private String required;
        private String upper;
        private String desc;
        
        public ParameterItem() {}
        // getter/setter 메서드들 모두 삭제 가능
    }
}