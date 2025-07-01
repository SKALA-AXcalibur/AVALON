package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioListDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.item.ScenarioItem;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;

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
    public ScenarioListDto getScenarioList(Integer projectKey, Integer offset, Integer query) {

        // 페이징 파라미터 검증
        validatePagingParameters(offset, query);
    
        // 총 개수 조회
        int total = scenarioRepository.countByProjectKey(projectKey);
        
        // offset, query 파라미터 기반 시나리오 목록 조회
        // offset: 시작점, query: 조회 개수
        List<ScenarioEntity> scenarios = scenarioRepository.findWithOffsetAndQuery(projectKey, offset, query);
        
        // DTO 변환
        List<ScenarioItem> scenarioItems = scenarios.stream()
            .map(scenario -> ScenarioItem.builder()
                .id(scenario.getScenarioId())
                .name(scenario.getName())
                .build())
            .collect(Collectors.toList());
        
        // DTO 반환
        return ScenarioListDto.builder()
            .scenarioList(scenarioItems)
            .total(total)
            .build();
    }
        
    /**
     * offset, query 파라미터 검증
     */
    @Override
    public void validatePagingParameters(Integer offset, Integer query) {
        // offset은 0 이상이어야 함
        if (offset < 0) {
            throw new BusinessExceptionHandler("offset은 0 이상이어야 합니다.", ErrorCode.BAD_REQUEST_ERROR);
        }
        // query는 1 이상이어야 함
        if (query < 1) {
            throw new BusinessExceptionHandler("query는 1 이상이어야 합니다.", ErrorCode.BAD_REQUEST_ERROR);
        }
    }
} 