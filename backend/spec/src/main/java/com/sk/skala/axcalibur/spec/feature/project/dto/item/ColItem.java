package com.sk.skala.axcalibur.spec.feature.project.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColItem {
    private String col_name; // 컬럼명
    private String desc; // 컬럼설명
    private String type; // 컬럼타입
    private Integer length; // 컬럼길이
    private Boolean isPk; // 기본키여부
    private String fk; // 외래키
    private Boolean isNull; // 널여부
    private String constraint; // 제약조건
}
