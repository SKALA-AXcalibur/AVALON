package com.sk.skala.axcalibur.apitest.feature.dto.request;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TestcaseResultUpdateDto(
        @Nullable @Schema(description = "헤더") @JsonDeserialize(as = LinkedMultiValueMap.class) MultiValueMap<String, String> header,
        @Nullable @Schema(description = "바디") @JsonDeserialize(as = HashMap.class) Map<String, Object> body,
        @NotNull @Schema(description = "성공 여부") Boolean success,
        @NotNull @Schema(description = "수행 시간") Double time) {

}
