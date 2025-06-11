package com.sk.skala.axcalibur.feature.dto;

public class RequirementInfo {
    private Long requirementId; // 요구사항 PK
    private String name;        // 요구사항 이름
    private String desc;        // 요구사항 설명
    private String priority;    // 요구사항 중요도
    private String major;       // 요구사항 대분류
    private String middle;      // 요구사항 중분류
    private String minor;       // 요구사항 소분류

    public RequirementInfo() {}

    public RequirementInfo(Long requirementId, String name, String desc, String priority, String major, String middle, String minor) {
        this.requirementId = requirementId;
        this.name = name;
        this.desc = desc;
        this.priority = priority;
        this.major = major;
        this.middle = middle;
        this.minor = minor;
    }

    //<editor-fold desc="Getter and Setter">
    public Long getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(Long requirementId) {
        this.requirementId = requirementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }
    //</editor-fold>
} 