package com.sk.skala.axcalibur.feature.testcase.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API parameter 정보 DTO
 * API의 파라미터 유형, 정보 저장
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiParamDto {
    private Integer paramId;        // 파라미터 ID
    private String category;        // 파라미터 항목
    private String koName;          // 한글명
    private String name;            // 영문명
    private String context;         // 항목유형 이름
    private String type;            // 데이터타입
    private Integer length;         // 길이
    private String format;          // 포맷
    private String defaultValue;    // 기본값
    private Boolean required;       // 필수여부
    private String parent;          // 상위항목명
    private String desc;            // 설명
}
