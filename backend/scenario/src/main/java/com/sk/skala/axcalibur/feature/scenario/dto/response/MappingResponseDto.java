package com.sk.skala.axcalibur.feature.scenario.dto.response;

import java.util.List;

import com.sk.skala.axcalibur.feature.scenario.dto.response.item.MappingItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FastAPI 매핑 생성 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MappingResponseDto {
    private List<MappingItem> mappingList;
}
