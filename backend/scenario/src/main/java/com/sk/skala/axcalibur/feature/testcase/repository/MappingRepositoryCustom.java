package com.sk.skala.axcalibur.feature.testcase.repository;

import java.util.List;

import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiListResponse;

public interface MappingRepositoryCustom {
    List<ApiListResponse> findApiListByScenarioId(String scenarioId);
}
