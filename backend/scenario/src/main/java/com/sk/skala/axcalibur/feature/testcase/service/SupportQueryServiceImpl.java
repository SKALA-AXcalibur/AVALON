package com.sk.skala.axcalibur.feature.testcase.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.feature.testcase.converter.ParameterConverter;
import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;
import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiListDto;
import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiListResponse;
import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiParamListResponse;
import com.sk.skala.axcalibur.feature.testcase.repository.ApiListRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.MappingRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.ParameterRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ApiListEntity;
import com.sk.skala.axcalibur.global.entity.ParameterEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TC 수동 추가 파트 중 보조 기능 구현 함수
 * - 시나리오 ID로부터 API 목록을 반환합니다.
 * - 선택한 API ID로부터 해당 API의 파라미터 목록을 반환합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SupportQueryServiceImpl implements SupportQueryService {
    private final ApiListRepository apiListRepository;
    private final MappingRepository mappingRepository;
    private final ParameterRepository parameterRepository;
    
    private final ParameterConverter parameterConverter;

    // 시나리오 ID로부터 API 목록 반환하는 함수
    @Override
    public ApiListResponse getApiListByScenario(String scenarioId) {
        List<ApiListDto> apiList = mappingRepository.findApiListByScenarioId(scenarioId);

        return ApiListResponse.builder()
            .apiList(apiList)
            .build();
    }

    // 선택한 API ID로부터 파라미터 목록 반환하는 함수
    @Override
    public ApiParamListResponse getParamsByApiId(String apiId) {
        ApiListEntity api = apiListRepository.findByApiListId(apiId)
            .orElseThrow(() -> new BusinessExceptionHandler(
                "해당 apiId를 찾을 수 없습니다: ", ErrorCode.NOT_FOUND_ERROR
            ));

        List<ParameterEntity> params = parameterRepository.findAllByApiListKey(api);
        List<ApiParamDto> dtoList = parameterConverter.toDto(params);

        return ApiParamListResponse.builder()
            .testDataList(dtoList)
            .build();
    }
}
