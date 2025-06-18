package com.sk.skala.axcalibur.scenario.apilist.feature.dto;

import lombok.Getter;

@Getter
public class ApiDto {
    private String apiName;
    private String url; 
    private String method;
    private String description;  // API 설명
    private Object parameters;
    private Object responseStructure;
}
