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
    private String apiName;             // API 이름
    private String url;                 // API 호출 경로
    private String method;              // API 호출 방법
    private String description;         // API 설명
    private Object parameters;          // API 파라미터
    private Object responseStructure;   // API 응답 구조
}
