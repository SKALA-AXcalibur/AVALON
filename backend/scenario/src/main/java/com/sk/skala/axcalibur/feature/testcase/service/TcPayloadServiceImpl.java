package com.sk.skala.axcalibur.feature.testcase.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiMappingDto;
import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;
import com.sk.skala.axcalibur.feature.testcase.dto.request.DbColumnDto;
import com.sk.skala.axcalibur.feature.testcase.dto.request.DbTableDto;
import com.sk.skala.axcalibur.feature.testcase.dto.request.ScenarioDto;
import com.sk.skala.axcalibur.feature.testcase.dto.request.TcRequestPayload;
import com.sk.skala.axcalibur.feature.testcase.entity.ApiListEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.DbColumnEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.DbDesignEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.MappingEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.ParameterEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.ScenarioEntity;
import com.sk.skala.axcalibur.feature.testcase.repository.DbColumnRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.DbDesignRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.MappingRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.ParameterRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.ScenarioRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
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
    private final DbDesignRepository dbDesignRepository;
    private final DbColumnRepository dbColumnRepository;
    private final MappingRepository mappingRepository;
    private final ParameterRepository parameterRepository;

    // project ID로부터 시나리오 객체 리스트 받아오는 함수
    @Override
    public List<ScenarioEntity> getScenarios(Integer projectId) {
        return scenarioRepository.findByProject_Id(projectId);
    }

    // project ID로부터 테이블 설계서 객체 리스트 받아오는 함수
    @Override
    public List<DbTableDto> getDbTableList(Integer projectId) {
        List<DbDesignEntity> dbTables = dbDesignRepository.findByProject_Id(projectId);

        if (dbTables == null || dbTables.isEmpty()) { // 테이블설계서가 조회되지 않는 경우
            throw new BusinessExceptionHandler("해당 프로젝트에 테이블 설계서가 존재하지 않습니다.", ErrorCode.NOT_FOUND_ERROR);
        }

        List<DbTableDto> dbList = new ArrayList<>();

        // project ID에 매핑된 테이블 설계서 정보 조회
        for (DbDesignEntity table : dbTables) {
            List<DbColumnEntity> columns = dbColumnRepository.findByDbDesign_Id(table.getId());
            
            // db_design의 ID로 column 조회 -> DbColumn DTO에 column 정보 매핑
            List<DbColumnDto> colList = columns.stream().map(col ->
                DbColumnDto.builder()
                    .name(col.getName())
                    .desc(col.getDescription())
                    .type(col.getType())
                    .length(col.getLength())
                    .isNull(col.isNull())
                    .fk(col.getFk())
                    .constraint(col.getConstraint())
                    .build()
            ).toList();

            dbList.add(DbTableDto.builder()
                .tableName(table.getName())
                .colList(colList)
                .build());
        }

        return dbList;
    }

    // fastAPI로 보낼 TcRequestPayload 형식의 객체 조합하는 함수
    @Override
    public TcRequestPayload buildPayload(ScenarioEntity scenario, List<DbTableDto> dbList) {
        // 1. API매핑표 관련 정보 조회
        List<MappingEntity> mappings = mappingRepository.findByScenarioKey_Id(scenario.getId());

        // 2. 매핑표에 해당하는 API 목록 조회
        List<ApiListEntity> apis = mappings.stream()
            .map(MappingEntity::getApiListKey)
            .collect(Collectors.toList());
        
        // 3. 각 API별로 파라미터 조회 및 DTO 조립
        List<ApiMappingDto> apiMappingList = apis.stream()
            .map(api -> {   // apis List 순회하며 각 api 별 DTO 생성
                List<ParameterEntity> params = parameterRepository.findByApiListKey_Key(api.getKey()); // 해당 API의 모든 파라미터 가져오는 작업

                List<ApiParamDto> paramDtoList = params.stream()
                    .map(param -> {
                        if (param.getCategoryKey() == null || param.getContextKey() == null) {
                            throw new BusinessExceptionHandler("파라미터의 category/context 정보가 유효하지 않습니다.", ErrorCode.NOT_VALID_ERROR);
                        }
                        
                        return ApiParamDto.builder() // 조회된 파라미터 리스트를 DTO로 변환
                            .category(param.getCategoryKey().getName()) // 카테고리 키 조회하여 카테고리 이름으로 반환
                            .koName(param.getNameKo())
                            .name(param.getName())
                            .context(param.getContextKey().getName())   // 항목 키 조회하여 항목 이름으로 반환
                            .type(param.getDataType())
                            .length(param.getLength())
                            .format(param.getFormat())
                            .defaultValue(param.getDefaultValue())
                            .required(param.getRequired())
                            .parent(param.getParentKey() != null ? param.getParentKey().getName() : null)
                            .desc(param.getDescription())
                            .build();
                    })
                    .collect(Collectors.toList());
                
                Integer step = mappings.stream()
                    .filter(m -> m.getApiListKey().getKey().equals(api.getKey()))
                    .findFirst()
                    .map(MappingEntity::getStep)
                    .orElseThrow(() ->
                        new BusinessExceptionHandler("API에 대한 매핑 정보가 존재하지 않습니다.", ErrorCode.NOT_FOUND_ERROR));
                
                return ApiMappingDto.builder()
                    .mappingID(api.getKey())
                    .step(step)
                    .url(api.getUrl())
                    .path(api.getPath())
                    .method(api.getMethod())
                    .desc(api.getDescription())
                    .paramList(paramDtoList)
                    .build();
            })
            .collect(Collectors.toList());
        
        // 4. 최종 요청 DTO 조립
        return TcRequestPayload.builder()
            .scenario(ScenarioDto.builder()
                .scenarioId(scenario.getScenarioId())
                .scenarioName(scenario.getName())
                .scenarioDesc(scenario.getDescription())
                .validation(scenario.getValidation())
                .build())
            .apiMappingList(apiMappingList)
            .dbList(dbList)
            .build();
    }
}
