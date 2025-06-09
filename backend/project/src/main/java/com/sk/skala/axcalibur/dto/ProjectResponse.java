package com.sk.skala.axcalibur.dto;

import java.util.List;

// 프로젝트 조회 응답 DTO (IF-PR-0002, IF-PR-0004)
// 설계서 기준: 상세한 명세서 분석 정보 포함
public class ProjectResponse {
    
    // 기본 응답 정보
    private String requestTime;       // 응답 시간 (responseTime)
    private String projectId;         // 프로젝트 ID
    private String avalon;           // 쿠키값
    
    // 명세서 분석 정보 (GET API 전용)
    private String projectName;      // 프로젝트명
    private List<String> specList;   // 명세서 목록
    private List<RequirementInfo> requirement;  // 요구사항 목록
    private List<ApiInfo> apiList;   // API 목록
    
    // 기본 생성자
    public ProjectResponse() {}
    
    // 단순 생성자 (IF-PR-0004용)
    public ProjectResponse(String requestTime, String projectId, String avalon) {
        this.requestTime = requestTime;
        this.projectId = projectId;
        this.avalon = avalon;
    }
    
    // 상세 생성자 (IF-PR-0002용)
    public ProjectResponse(String requestTime, String projectId, String avalon, 
                          String projectName, List<String> specList, 
                          List<RequirementInfo> requirement, List<ApiInfo> apiList) {
        this.requestTime = requestTime;
        this.projectId = projectId;
        this.avalon = avalon;
        this.projectName = projectName;
        this.specList = specList;
        this.requirement = requirement;
        this.apiList = apiList;
    }
    
    // getter
    public String getRequestTime() {
        return requestTime;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public String getAvalon() {
        return avalon;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public List<String> getSpecList() {
        return specList;
    }
    
    public List<RequirementInfo> getRequirement() {
        return requirement;
    }
    
    public List<ApiInfo> getApiList() {
        return apiList;
    }
    
    // 요구사항 정보 (req 객체)
    public static class RequirementInfo {
        private String name;        // 요구사항 이름
        private String desc;        // 요구사항 설명
        private String priority;    // 요구사항 중요도
        private String major;       // 요구사항 대분류
        private String middle;      // 요구사항 중분류
        private String minor;       // 요구사항 소분류
        
        public RequirementInfo() {}
        
        public RequirementInfo(String name, String desc, String priority, String major, String middle, String minor) {
            this.name = name;
            this.desc = desc;
            this.priority = priority;
            this.major = major;
            this.middle = middle;
            this.minor = minor;
        }
        
        // getter
        public String getName() {
            return name;
        }
        
        public String getDesc() {
            return desc;
        }
        
        public String getPriority() {
            return priority;
        }
        
        public String getMajor() {
            return major;
        }
        
        public String getMiddle() {
            return middle;
        }
        
        public String getMinor() {
            return minor;
        }
    }
    
    // API 정보 (api 객체)
    public static class ApiInfo {
        private String id;          // API 아이디
        private String name;        // API 이름
        private String desc;        // API 설명
        private String method;      // API HTTP Method
        private String url;         // API URL
        private String path;        // API Path
        private String pathQuery;   // API Path/Query
        private ParameterGroup request;   // API Request
        private ParameterGroup response;  // API Response
        
        public ApiInfo() {}
        
        // getter
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDesc() {
            return desc;
        }
        
        public String getMethod() {
            return method;
        }
        
        public String getUrl() {
            return url;
        }
        
        public String getPath() {
            return path;
        }
        
        public String getPathQuery() {
            return pathQuery;
        }
        
        public ParameterGroup getRequest() {
            return request;
        }
        
        public ParameterGroup getResponse() {
            return response;
        }
        
        // setter
        public void setId(String id) {
            this.id = id;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public void setDesc(String desc) {
            this.desc = desc;
        }
        
        public void setMethod(String method) {
            this.method = method;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public void setPath(String path) {
            this.path = path;
        }
        
        public void setPathQuery(String pathQuery) {
            this.pathQuery = pathQuery;
        }
        
        public void setRequest(ParameterGroup request) {
            this.request = request;
        }
        
        public void setResponse(ParameterGroup response) {
            this.response = response;
        }
    }
    
    // 파라미터 그룹 (Path/Query, Request, Response 객체)
    public static class ParameterGroup {
        private List<ParameterDetail> pq;   // Path/Query 객체
        private List<ParameterDetail> req;  // Request 객체  
        private List<ParameterDetail> res;  // Response 객체
        
        public ParameterGroup() {}
        
        // getter
        public List<ParameterDetail> getPq() {
            return pq;
        }
        
        public List<ParameterDetail> getReq() {
            return req;
        }
        
        public List<ParameterDetail> getRes() {
            return res;
        }
        
        // setter
        public void setPq(List<ParameterDetail> pq) {
            this.pq = pq;
        }
        
        public void setReq(List<ParameterDetail> req) {
            this.req = req;
        }
        
        public void setRes(List<ParameterDetail> res) {
            this.res = res;
        }
    }
    
    // 파라미터 상세 정보 (내부 클래스)
    public static class ParameterDetail 
    {
        private String korName;     // 한글명
        private String name;        // 영문명
        private String itemType;    // 항목유형
        private String step;        // 단계
        private String dataType;    // 데이터타입
        private String length;      // 길이
        private String format;      // 포맷
        private String defaultValue; // 기본값 (default는 예약어)
        private String required;    // 필수여부
        private String upper;       // 상위항목명
        private String desc;        // 설명
        
        public ParameterDetail() {}
        
        // getter
        public String getKorName() {
            return korName;
        }
        
        public String getName() {
            return name;
        }
        
        public String getItemType() {
            return itemType;
        }
        
        public String getStep() {
            return step;
        }
        
        public String getDataType() {
            return dataType;
        }
        
        public String getLength() {
            return length;
        }
        
        public String getFormat() {
            return format;
        }
        
        public String getDefaultValue() {
            return defaultValue;
        }
        
        public String getRequired() {
            return required;
        }
        
        public String getUpper() {
            return upper;
        }
        
        public String getDesc() {
            return desc;
        }
        
        // setter
        public void setKorName(String korName) {
            this.korName = korName;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public void setItemType(String itemType) {
            this.itemType = itemType;
        }
        
        public void setStep(String step) {
            this.step = step;
        }
        
        public void setDataType(String dataType) {
            this.dataType = dataType;
        }
        
        public void setLength(String length) {
            this.length = length;
        }
        
        public void setFormat(String format) {
            this.format = format;
        }
        
        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
        
        public void setRequired(String required) {
            this.required = required;
        }
        
        public void setUpper(String upper) {
            this.upper = upper;
        }
        
        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}