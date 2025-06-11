package com.sk.skala.axcalibur.feature.dto;

public class ParameterDetailDTO {
    private Long parameterId;       // 파라미터 PK
    private String korName;         // 한글명
    private String name;            // 영문명
    private String itemType;        // 항목유형
    private Integer step;           // 단계 (int)
    private String dataType;        // 데이터타입
    private Integer length;         // 길이 (int)
    private String format;          // 포맷
    private String defaultValue;    // 기본값
    private Boolean required;       // 필수여부 (boolean)
    private String upper;           // 상위항목명
    private String desc;            // 설명

    // 코멘트 반영: 어떤 API에 속한 파라미터인지 명시
    private String apiId;           // 소속 API ID
    private String apiName;         // 소속 API 이름

    //<editor-fold desc="Getter and Setter">
    public Long getParameterId() {
        return parameterId;
    }

    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
    }

    public String getKorName() {
        return korName;
    }

    public void setKorName(String korName) {
        this.korName = korName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getUpper() {
        return upper;
    }

    public void setUpper(String upper) {
        this.upper = upper;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }
    //</editor-fold>
} 