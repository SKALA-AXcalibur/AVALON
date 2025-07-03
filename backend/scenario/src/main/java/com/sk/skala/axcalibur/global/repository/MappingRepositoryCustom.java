package com.sk.skala.axcalibur.global.repository;

import java.util.List;

import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiListDto;

public interface MappingRepositoryCustom {
    List<ApiListDto> findApiListByScenarioId(String scenarioId);
}
