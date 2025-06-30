package com.sk.skala.axcalibur.feature.testcase.service;

import java.util.List;

import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;
import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiListResponse;

public interface SupportQueryService {
    List<ApiListResponse> getApiListByScenario(String scenarioId);
    
    List<ApiParamDto> getParamsByApiId(String apiId);
}
