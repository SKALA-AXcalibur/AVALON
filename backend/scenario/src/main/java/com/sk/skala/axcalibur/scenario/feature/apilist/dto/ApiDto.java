package com.sk.skala.axcalibur.scenario.feature.apilist.dto;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiDto {
    private String apiName;
    private String url; 
    private String method;
    private String description;  // API 설명
    private Object parameters;
    private Object responseStructure;
}
