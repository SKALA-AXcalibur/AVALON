package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
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
 * 시나리오 목록 조회 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioListServiceImpl implements ScenarioListService {

    private final ScenarioRepository scenarioRepository;

    @Override
    public ScenarioListDto getScenarioList(Integer projectKey, int offset, int query) {

        validatePagingParameters(offset, query);
    
        // 총 개수 조회
        int total = scenarioRepository.countByProject_Id(projectKey);
        
        // 페이징 객체 생성 (offset 기반)
        PageRequest pageRequest = PageRequest.of(offset / query, query);
        
        // 시나리오 목록 조회
        List<ScenarioEntity> scenarios = scenarioRepository.findByProject_IdOrderByCreateAtDesc(projectKey, pageRequest);
        
        // DTO 변환
        List<ScenarioItem> scenarioItems = scenarios.stream()
            .map(scenario -> ScenarioItem.builder()
                .id(scenario.getScenarioId())
                .name(scenario.getName())
                .build())
            .collect(Collectors.toList());
        
        return ScenarioListDto.builder()
            .scenarioList(scenarioItems)
            .total(total)
            .build();
    }
        
    /**
     * offset, query 파라미터 검증
     */
    @Override
    public void validatePagingParameters(int offset, int query) {
        if (offset < 0) {
            throw new BusinessExceptionHandler("offset은 0 이상이어야 합니다.", ErrorCode.BAD_REQUEST_ERROR);
        }
        if (query < 1 || query > 100) {
            throw new BusinessExceptionHandler("query는 1 이상이어야 합니다.", ErrorCode.BAD_REQUEST_ERROR);
        }
        if (offset % query != 0) {
            throw new BusinessExceptionHandler("offset은 query의 배수여야 합니다.", ErrorCode.BAD_REQUEST_ERROR);
        }
    }
} 