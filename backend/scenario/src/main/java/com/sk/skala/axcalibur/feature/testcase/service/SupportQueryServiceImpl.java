package com.sk.skala.axcalibur.feature.testcase.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.feature.testcase.converter.ParameterConverter;
import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;
import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiListResponse;
import com.sk.skala.axcalibur.feature.testcase.repository.ApiListRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.MappingRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.ParameterRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ApiListEntity;
import com.sk.skala.axcalibur.global.entity.ParameterEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportQueryServiceImpl implements SupportQueryService {
    private final ApiListRepository apiListRepository;
    private final MappingRepository mappingRepository;
    private final ParameterRepository parameterRepository;
    
    private final ParameterConverter parameterConverter;

    @Override
    public List<ApiListResponse> getApiListByScenario(String scenarioId) {
        return mappingRepository.findApiListByScenarioId(scenarioId);
    }

    @Override
    public List<ApiParamDto> getParamsByApiId(String apiId) {
        ApiListEntity api = apiListRepository.findByApiListId(apiId)
            .orElseThrow(() -> new BusinessExceptionHandler(
                "해당 apiId를 찾을 수 없습니다: ", ErrorCode.NOT_FOUND_ERROR
            ));

        List<ParameterEntity> params = parameterRepository.findAllByApiListKey(api);

        return parameterConverter.toDto(params);
    }
}
