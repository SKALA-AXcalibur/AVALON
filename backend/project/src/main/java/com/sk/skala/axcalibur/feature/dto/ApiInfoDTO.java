package com.sk.skala.axcalibur.feature.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiInfoDTO {
    private Long apiPk;         // API PK
    private String id;          // API 아이디
    private String name;        // API 이름
    private String desc;        // API 설명
    private String method;      // API HTTP Method
    private String url;         // API URL
    private String path;        // API Path
    private List<ParameterDetailDTO> pathQuery;   // API Path/Query (Array)
    private List<ParameterDetailDTO> request;     // API Request (Array)
    private List<ParameterDetailDTO> response;    // API Response (Array)

} 