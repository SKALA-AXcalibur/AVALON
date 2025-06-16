package com.sk.skala.axcalibur.feature.project.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiInfoDto {
    private Long apiPk;         // API PK
    private String id;          // API 아이디
    private String name;        // API 이름
    private String desc;        // API 설명
    private String method;      // API HTTP Method
    private String url;         // API URL
    private String path;        // API Path
    private List<ParameterDetailDto> pathQuery;   // API Path/Query (Array)
    private List<ParameterDetailDto> request;     // API Request (Array)
    private List<ParameterDetailDto> response;    // API Response (Array)

}