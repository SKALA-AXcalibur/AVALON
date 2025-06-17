package com.sk.skala.axcalibur.feature.testcase.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DB Column정보 DTO
 * 테이블 별 Column 정보를 저장하기 위한 DTO 객체 정의
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbColumnDto {
    private String name;
    private String desc;
    private String type;
    private Integer length;
    private Boolean isNull;
    private String fk;
    private String constraint;
}
