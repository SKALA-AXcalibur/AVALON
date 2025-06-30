package com.sk.skala.axcalibur.feature.testcase.service;

import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiListResponse;
import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiParamListResponse;

public interface SupportQueryService {
    ApiListResponse getApiListByScenario(String scenarioId);
    
    ApiParamListResponse getParamsByApiId(String apiId);
}
