package com.sk.skala.axcalibur.scenario.apilist.feature.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiMappingDto {
    private String scenarioId;           // 시나리오 ID
    private String stepName;             // 시나리오 단계 명칭
    private String apiName;              // API의 기능을 나타내는 이름
    private String description;          // API에 대한 설명
    private String url;                  // API의 호출 경로
    private String method;               // API 호출 방식
    private Object parameters;           // 파라미터 정보
    private Object responseStructure;    // 응답 구조
}