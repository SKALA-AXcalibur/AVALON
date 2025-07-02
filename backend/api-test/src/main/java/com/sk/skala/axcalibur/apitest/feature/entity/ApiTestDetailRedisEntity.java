package com.sk.skala.axcalibur.apitest.feature.entity;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@RedisHash(value = "avalon_api_test_detail", timeToLive = 172800) // TTL 기간 (이틀)
@Builder(toBuilder = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiTestDetailRedisEntity {
    @Id
    @NotBlank
    @Schema(description = "시나리오 pk - 단계 - 응답코드")
    private String id;
    @NotNull
    @Schema(description = "testcase_result pk")
    private Integer resultId;
    @NotNull
    @Schema(description = "API path")
    private Map<String, String> path;
    @NotNull
    @Schema(description = "API 쿼리 파라미터")
    private MultiValueMap<String, String> query;
    @NotNull
    @Schema(description = "API 응답 헤더")
    @JsonDeserialize(as = LinkedMultiValueMap.class)
    private MultiValueMap<String, String> header;
    @NotNull
    @Schema(description = "API 응답 바디")
    @JsonDeserialize(as = HashMap.class)
    private Map<String, Object> body;

}
