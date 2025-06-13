package com.sk.skala.axcalibur.feature.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDetailDto {
    private String id;              // 파라미터 ID
    private String korName;         // 한글명
    private String name;            // 영문명
    private String itemType;        // 항목유형
    private Integer step;           // 단계 (int)
    private String dataType;        // 데이터타입
    private Integer length;         // 길이 (int)
    private String format;          // 포맷
    private String defaultValue;    // 기본값
    private Boolean required;       // 필수여부 (boolean)
    private Integer upper;          // 상위항목명
    private String desc;            // 설명

    // 코멘트 반영: 어떤 API에 속한 파라미터인지 명시
    private String apiId;           // 소속 API ID
    private String apiName;         // 소속 API 이름

} 