package com.sk.skala.axcalibur.feature.testcase.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiMappingDto;
import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;
import com.sk.skala.axcalibur.feature.testcase.dto.request.ScenarioDto;
import com.sk.skala.axcalibur.feature.testcase.dto.request.TcRequestPayload;
import com.sk.skala.axcalibur.feature.testcase.repository.MappingRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.ParameterRepository;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ApiListEntity;
import com.sk.skala.axcalibur.global.entity.MappingEntity;
import com.sk.skala.axcalibur.global.entity.ParameterEntity;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TC 생성 이전 단계의 서비스
 * project ID로부터 DB를 조회하여 TC 생성에 필요한 정보를 조합합니다.
 * payload를 조합하는 부분의 실제 구현부입니다.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class TcPayloadServiceImpl implements TcPayloadService{
    private final ScenarioRepository scenarioRepository;
    private final MappingRepository mappingRepository;
    private final ParameterRepository parameterRepository;

    // project ID로부터 시나리오 객체 리스트 받아오는 함수
    @Override
    public List<ScenarioEntity> getScenarios(Integer projectId) {
        return scenarioRepository.findByProject_Id(projectId);
    }

    // fastAPI로 보낼 TcRequestPayload 형식의 객체 조합하는 함수
    @Override
    public TcRequestPayload buildPayload(ScenarioEntity scenario) {
        // API매핑표 관련 정보 조회
        List<MappingEntity> mappings = mappingRepository.findByScenarioKey_Id(scenario.getId());

        // 해당 시나리오에서 참조되는 모든 API id 모음
        List<Integer> apiIds = mappings.stream()
                                        .map(m -> m.getApiListKey().getId())
                                        .toList();

        // 필요한 파라미터 정보 미리 가져오기
        Map<Integer, List<ParameterEntity>> paramMapByApiId =
                parameterRepository.findByApiListKey_IdIn(apiIds).stream()
                .collect(Collectors.groupingBy(p -> p.getApiListKey().getId()));

        // 각 API별로 파라미터 조회 및 DTO 조립
        List<ApiMappingDto> apiMappingList = mappings.stream()
        .map(mapping -> {

            ApiListEntity api = mapping.getApiListKey();
            List<ParameterEntity> params = paramMapByApiId
                    .getOrDefault(api.getId(), Collections.emptyList());

            // 파라미터 DTO 변환
            List<ApiParamDto> paramDtoList = params.stream()
                .map(param -> {
                    if (param.getCategoryKey() == null || param.getContextKey() == null) {
                        throw new BusinessExceptionHandler(
                                "파라미터의 category/context 정보가 유효하지 않습니다.",
                                ErrorCode.NOT_VALID_ERROR);
                    }
                    return ApiParamDto.builder()
                            .paramId(param.getId())
                            .category(param.getCategoryKey().getName())
                            .koName(param.getNameKo())
                            .name(param.getName())
                            .context(param.getContextKey().getName())
                            .type(param.getDataType())
                            .length(param.getLength())
                            .format(param.getFormat())
                            .defaultValue(param.getDefaultValue())
                            .required(param.getRequired())
                            .parent(param.getParentKey() != null
                                    ? param.getParentKey().getName()
                                    : null)
                            .desc(param.getDescription())
                            .build();
                })
                .toList();

            // ApiMappingDto 변환
            return ApiMappingDto.builder()
                    .mappingId(mapping.getId())
                    .step(mapping.getStep())
                    .name(api.getName())
                    .url(api.getUrl())
                    .path(api.getPath())
                    .method(api.getMethod())
                    .desc(api.getDescription())
                    .paramList(paramDtoList)
                    .build();
        })
        .toList();

        
        // 3. 최종 요청 DTO 조립
        return TcRequestPayload.builder()
            .scenario(ScenarioDto.builder()
                .scenarioName(scenario.getName())
                .scenarioDesc(scenario.getDescription())
                .validation(scenario.getValidation())
                .build())
            .apiMappingList(apiMappingList)
            .build();
    }
}
