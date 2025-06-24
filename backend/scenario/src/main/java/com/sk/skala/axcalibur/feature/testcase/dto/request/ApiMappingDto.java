package com.sk.skala.axcalibur.feature.testcase.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API 정보 DTO
 * API 매핑표 관련 객체 정의
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiMappingDto {
    private Integer mappingId;           // 매핑표 ID
    private Integer step;                // 단계명(시나리오 내 호출 순서)
    private String name;                 // API이름
    private String url;                  // URL
    private String path;                 // path
    private String method;               // HTTP Method
    private String desc;                 // 설명

    private List<ApiParamDto> paramList; // parameter 정보
}
