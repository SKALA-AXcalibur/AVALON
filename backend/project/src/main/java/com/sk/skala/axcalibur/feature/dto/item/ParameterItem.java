package com.sk.skala.axcalibur.feature.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterItem {
    private String korName;     // 한글명
    private String name;        // 영문명
    private String itemType;    // 항목유형
    private String step;        // 단계  
    private String dataType;    // 데이터타입
    private String length;      // 길이
    private String format;      // 포맷
    private String defaultValue; // 기본값
    private String required;    // 필수여부
    private String upper;       // 상위항목명
    private String desc;        // 설명
}