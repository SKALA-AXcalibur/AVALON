package com.sk.skala.axcalibur.feature.dto;

import lombok.Getter;

@Getter
public class RequirementInfoDto {
    private Long requirementId; // 요구사항 PK
    private String name;        // 요구사항 이름
    private String desc;        // 요구사항 설명
    private String priority;    // 요구사항 중요도
    private String major;       // 요구사항 대분류
    private String middle;      // 요구사항 중분류
    private String minor;       // 요구사항 소분류

    public RequirementInfoDto() {}

    public RequirementInfoDto(Long requirementId, String name, String desc, String priority, String major, String middle, String minor) {
        this.requirementId = requirementId;
        this.name = name;
        this.desc = desc;
        this.priority = priority;
        this.major = major;
        this.middle = middle;
        this.minor = minor;
    }
} 