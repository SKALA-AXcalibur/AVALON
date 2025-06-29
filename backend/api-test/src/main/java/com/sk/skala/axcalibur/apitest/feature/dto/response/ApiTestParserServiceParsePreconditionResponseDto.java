package com.sk.skala.axcalibur.apitest.feature.dto.response;

import java.util.Map;

import org.springframework.util.MultiValueMap;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record ApiTestParserServiceParsePreconditionResponseDto(
        @NotNull @Schema(description = "path 값") Map<String, String> path,
        @NotNull @Schema(description = "query 파라미터, 쿼리 키는 중복 허용") MultiValueMap<String, String> query,
        @NotNull @Schema(description = "header") MultiValueMap<String, String> header,
        @NotNull @Schema(description = "body") Map<String, Object> body) {

}
