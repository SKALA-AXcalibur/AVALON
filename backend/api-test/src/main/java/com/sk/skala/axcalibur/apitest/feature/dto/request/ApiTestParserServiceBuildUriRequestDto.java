package com.sk.skala.axcalibur.apitest.feature.dto.request;

import java.util.Map;

import org.springframework.util.MultiValueMap;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "ApiTestParserService BuildUri Response DTO")
public record ApiTestParserServiceBuildUriRequestDto(
                @NotBlank @Schema(description = "uri") String uri,
                @NotNull @Schema(description = "path 값") Map<String, String> path,
                @NotNull @Schema(description = "query 값, 쿼리 키는 중복 허용") MultiValueMap<String, String> query) {

}
