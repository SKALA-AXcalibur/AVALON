package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioListDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.item.ScenarioListItem;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시나리오 목록 조회 서비스 구현체(IF-SN-0009)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioListServiceImpl implements ScenarioListService {

    private final ScenarioRepository scenarioRepository;

    @Override
    public SuccessResponse<ScenarioListDto> getScenarioList(Integer projectKey) {
        // 프로젝트별 시나리오 목록 조회 (생성일시 기준 내림차순)
        List<ScenarioEntity> scenarios = scenarioRepository.findByProject_IdOrderByCreateAtDesc(projectKey);
        
        // DTO 변환
        List<ScenarioListItem> scenarioItems = scenarios.stream()
            .map(scenario -> ScenarioListItem.builder()
                .id(scenario.getScenarioId())
                .name(scenario.getName())
                .build())
            .collect(Collectors.toList());
        
        // DTO 생성
        ScenarioListDto scenarioListDto = ScenarioListDto.builder()
            .scenarioList(scenarioItems)
            .total(scenarioItems.size())
            .build();
        
        // SuccessResponse로 감싸서 반환
        return SuccessResponse.<ScenarioListDto>builder()
            .data(scenarioListDto)
            .status(SuccessCode.SELECT_SUCCESS)
            .message("시나리오 목록 조회가 완료되었습니다.")
            .build();
    }
} 