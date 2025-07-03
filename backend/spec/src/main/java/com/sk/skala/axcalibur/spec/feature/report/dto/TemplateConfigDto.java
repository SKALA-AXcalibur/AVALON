package com.sk.skala.axcalibur.spec.feature.report.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplateConfigDto {
    private String templateType;
    private List<TemplateMappingDto> mapping;
}
