package com.sk.skala.axcalibur.feature.testcase.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiListResponse;
import com.sk.skala.axcalibur.feature.testcase.repository.MappingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportQueryServiceImpl implements SupportQueryService {
    private final MappingRepository mappingRepository;

    @Override
    public List<ApiListResponse> getApiListByScenario(String scenarioId) {
        List<ApiListResponse> response = mappingRepository.findApiListByScenarioId(scenarioId);

        return response;
    }
}
