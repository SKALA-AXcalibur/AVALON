package com.sk.skala.axcalibur.feature.dto;

import java.util.List;

public class ApiInfo {
    private Long apiPk;         // API PK
    private String id;          // API 아이디
    private String name;        // API 이름
    private String desc;        // API 설명
    private String method;      // API HTTP Method
    private String url;         // API URL
    private String path;        // API Path
    private List<ParameterDetail> pathQuery;   // API Path/Query (Array)
    private List<ParameterDetail> request;     // API Request (Array)
    private List<ParameterDetail> response;    // API Response (Array)

    //<editor-fold desc="Getter and Setter">
    public Long getApiPk() {
        return apiPk;
    }

    public void setApiPk(Long apiPk) {
        this.apiPk = apiPk;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<ParameterDetail> getPathQuery() {
        return pathQuery;
    }

    public void setPathQuery(List<ParameterDetail> pathQuery) {
        this.pathQuery = pathQuery;
    }

    public List<ParameterDetail> getRequest() {
        return request;
    }

    public void setRequest(List<ParameterDetail> request) {
        this.request = request;
    }

    public List<ParameterDetail> getResponse() {
        return response;
    }

    public void setResponse(List<ParameterDetail> response) {
        this.response = response;
    }
    //</editor-fold>
} 