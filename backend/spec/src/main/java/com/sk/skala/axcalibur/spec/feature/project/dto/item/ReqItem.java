package com.sk.skala.axcalibur.spec.feature.project.dto.item;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqItem {
    @NotBlank(message = "요구사항 아이디는 필수 입력 항목입니다.")
    private String id;          // 요구사항 아이디
    private String name;        // 요구사항 이름
    private String desc;        // 요구사항 설명  
    private String priority;    // 요구사항 우선도
    private String major;       // 요구사항 대분류
    private String middle;      // 요구사항 중분류
    private String minor;       // 요구사항 소분류
}