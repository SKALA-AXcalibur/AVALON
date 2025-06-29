package com.sk.skala.axcalibur.feature.scenario.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class ScenarioListRequestDto {
    private int offset = 0; // 조회 시작 위치
    private int query = 10; // 조회 개수 // 조회 개수
} 