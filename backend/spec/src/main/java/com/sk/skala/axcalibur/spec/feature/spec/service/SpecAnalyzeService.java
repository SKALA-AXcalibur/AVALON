package com.sk.skala.axcalibur.spec.feature.spec.service;

import java.util.Map;

import com.sk.skala.axcalibur.spec.feature.spec.enums.FileTypeName;
import com.sk.skala.axcalibur.spec.global.entity.ProjectEntity;

public interface SpecAnalyzeService {
    Map<String, String> analyze(ProjectEntity project);
    String getPath(ProjectEntity project, FileTypeName typeName);
}