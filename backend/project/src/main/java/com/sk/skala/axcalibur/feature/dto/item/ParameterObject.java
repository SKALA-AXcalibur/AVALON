package com.sk.skala.axcalibur.feature.dto.item;

import lombok.Getter;

@Getter
public class ParameterObject {
    // Step 4 → Step 5: 실제 파라미터 상세 정보
    private String korName;      // Step 5
    private String name;         // Step 5
    private String itemType;     // Step 5
    private String step;         // Step 5
    private String dataType;     // Step 5
    private String length;       // Step 5
    private String format;       // Step 5
    private String defaultValue; // Step 5
    private String required;     // Step 5
    private String upper;        // Step 5
    private String desc;         // Step 5
    
    public ParameterObject() {}
}