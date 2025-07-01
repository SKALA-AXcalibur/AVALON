package com.sk.skala.axcalibur.apitest.feature.service;

import java.text.ParseException;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTestParserServiceBuildUriRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTestParserServiceParsePreconditionRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ApiTestParserServiceParsePreconditionResponseDto;

public interface ApiTestParserService {
    ApiTestParserServiceParsePreconditionResponseDto parsePrecondition(
            ApiTestParserServiceParsePreconditionRequestDto dto) throws ParseException;

    String buildUri(ApiTestParserServiceBuildUriRequestDto dto);

}
