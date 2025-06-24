package com.sk.skala.axcalibur.feature.testcase.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TC생성요청 DTO
 * 각 Entity별 객체 조합하여 TC생성서버(fastAPI)에 전달하는 역할
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TcRequestPayload {
    private ScenarioDto scenario;
    private List<ApiMappingDto> apiMappingList;
}
