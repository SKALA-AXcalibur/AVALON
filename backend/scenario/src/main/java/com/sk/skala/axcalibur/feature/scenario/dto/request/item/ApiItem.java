package com.sk.skala.axcalibur.feature.scenario.dto.request.item;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiItem {
    
    private String id;                          // API 아이디
    private String name;                        // API 이름
    private String desc;                        // API 설명
    private String method;                      // API HTTP Method
    private String url;                         // API URL
    private String path;                        // API Path
    private String reqId;                       // 요구사항 아이디

    private List<ParameterItem> pathQuery;      // Path/Query 파라미터 목록
    private List<ParameterItem> request;        // Request 파라미터 목록  
    private List<ParameterItem> response;       // Response 파라미터 목록
}