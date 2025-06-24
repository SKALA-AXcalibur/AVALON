package com.sk.skala.axcalibur.apitest.feature.entity;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@RedisHash(value = "avalon_api_test", timeToLive = 172800) // TTL 기간 (이틀)
@Builder(toBuilder = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiTestRedisEntity {
    @Id
    @NotNull
    @Schema(description = "scenario pk")
    private Integer id;
    @Builder.Default
    @NotNull
    @Schema(description = "완료된 단계")
    private Integer completed = 0;
    @NotNull
    @Schema(description = "최종 단계")
    private Integer finish;
}