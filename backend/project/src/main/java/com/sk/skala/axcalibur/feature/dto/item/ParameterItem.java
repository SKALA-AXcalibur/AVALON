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
    // Step 4 → Step 5: 실제 파라미터 상세 정보
    private String korName;     // 한글명
    private String name;        // 영문명
    private String itemType;    // 항목유형
    private Integer step;       // 단계  
    private String dataType;    // 데이터타입
    private Integer length;     // 길이
    private String format;      // 포맷
    private String defaultValue; // 기본값
    private Boolean required;   // 필수여부
    private Integer upper;      // 상위항목명
    private String desc;        // 설명
}