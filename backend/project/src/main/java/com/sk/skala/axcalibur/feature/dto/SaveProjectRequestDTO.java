package com.sk.skala.axcalibur.feature.dto;

import java.util.List;

// 프로젝트 목록 저장 요청 DTO (IF-PR-0001)
// 설계서 기준: 상세한 요구사항 및 API 정보 포함
public class SaveProjectRequestDTO {
    
    // 요구사항 관련
    private List<RequirementItem> requirement;  // 요구사항 목록
    
    // API 관련
    private List<ApiItem> apiList;             // API 목록
    
    // 기본 생성자
    public SaveProjectRequestDTO() {}
    
    // getter/setter (Spring JSON 파싱용)
    public List<RequirementItem> getRequirement() {
        return requirement;
    }
    
    public void setRequirement(List<RequirementItem> requirement) {
        this.requirement = requirement;
    }
    
    public List<ApiItem> getApiList() {
        return apiList;
    }
    
    public void setApiList(List<ApiItem> apiList) {
        this.apiList = apiList;
    }
    
    // 요구사항 아이템 (req 객체) - 설계서 기준 확장
    public static class RequirementItem {
        private String name;        // 요구사항 이름
        private String desc;        // 요구사항 설명
        private String priority;    // 요구사항 중요도
        private String major;       // 요구사항 대분류
        private String middle;      // 요구사항 중분류
        private String minor;       // 요구사항 소분류
        
        public RequirementItem() {}
        
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
        
        // setter
        public void setName(String name) {
            this.name = name;
        }
        
        public void setDesc(String desc) {
            this.desc = desc;
        }
        
        public void setPriority(String priority) {
            this.priority = priority;
        }
        
        public void setMajor(String major) {
            this.major = major;
        }
        
        public void setMiddle(String middle) {
            this.middle = middle;
        }
        
        public void setMinor(String minor) {
            this.minor = minor;
        }
    }
    
    // API 아이템 (api 객체) - 설계서 기준 확장
    public static class ApiItem {
        private String id;          // API 아이디
        private String name;        // API 이름
        private String desc;        // API 설명
        private String method;      // HTTP Method
        private String url;         // API URL
        private String path;        // API Path
        private String pathQuery;   // API Path/Query
        private ParameterGroup request;   // API Request
        private ParameterGroup response;  // API Response
        
        public ApiItem() {}
        
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
        private List<ParameterItem> pq;   // Path/Query 객체
        private List<ParameterItem> req;  // Request 객체  
        private List<ParameterItem> res;  // Response 객체
        
        public ParameterGroup() {}
        
        // getter
        public List<ParameterItem> getPq() {
            return pq;
        }
        
        public List<ParameterItem> getReq() {
            return req;
        }
        
        public List<ParameterItem> getRes() {
            return res;
        }
        
        // setter
        public void setPq(List<ParameterItem> pq) {
            this.pq = pq;
        }
        
        public void setReq(List<ParameterItem> req) {
            this.req = req;
        }
        
        public void setRes(List<ParameterItem> res) {
            this.res = res;
        }
    }
    
    // 파라미터 아이템 내부 클래스 - 설계서 기준 전체 필드
    public static class ParameterItem {
        private String korName;     // 한글명
        private String name;        // 영문명
        private String itemType;    // 항목유형
        private String step;        // 단계
        private String dataType;    // 데이터타입
        private String length;      // 길이
        private String format;      // 포맷
        private String defaultValue; // 기본값 (default는 예약어라 defaultValue 사용)
        private String required;    // 필수여부
        private String upper;       // 상위항목명
        private String desc;        // 설명
        
        public ParameterItem() {}
        
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