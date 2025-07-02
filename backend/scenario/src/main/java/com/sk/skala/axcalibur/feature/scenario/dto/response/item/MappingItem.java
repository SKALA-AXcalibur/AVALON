package com.sk.skala.axcalibur.feature.scenario.dto.response.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI 서비스로부터 받는 매핑 아이템 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MappingItem {
    private String scenarioId;  // 시나리오 ID
    private String apiName;     // API 이름
    private Integer step;       // 매핑 단계
} 