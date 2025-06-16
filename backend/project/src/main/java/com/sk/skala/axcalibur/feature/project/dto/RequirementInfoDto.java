package com.sk.skala.axcalibur.feature.project.dto;

import lombok.Getter;

@Getter
public class RequirementInfoDto {
    private String id;          // 요구사항 ID
    private String name;        // 요구사항 이름
    private String desc;        // 요구사항 설명
    private String priority;    // 요구사항 중요도
    private String major;       // 요구사항 대분류
    private String middle;      // 요구사항 중분류
    private String minor;       // 요구사항 소분류

    public RequirementInfoDto() {}

    public RequirementInfoDto(String id, String name, String desc, String priority, String major, String middle, String minor) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.priority = priority;
        this.major = major;
        this.middle = middle;
        this.minor = minor;
    }
} 