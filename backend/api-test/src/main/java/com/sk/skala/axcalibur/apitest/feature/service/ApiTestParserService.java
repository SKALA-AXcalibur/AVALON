package com.sk.skala.axcalibur.apitest.feature.service;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTestParserServiceBuildUriRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ApiTestParserServiceParsePreconditionResponseDto;

public interface ApiTestParserService {
    ApiTestParserServiceParsePreconditionResponseDto parsePrecondition(String precondition);

    String buildUri(ApiTestParserServiceBuildUriRequestDto dto);

}
