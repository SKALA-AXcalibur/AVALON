package com.sk.skala.axcalibur.feature.apilist.dto;

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
}
