package com.sk.skala.axcalibur.spec.feature.report.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplateMappingDto {
    private String fieldName;
    private String cellPosition;
    private String dataType;
    private int rowIndex;
    private int columnIndex;
}
