package com.sk.skala.axcalibur.feature.scenario.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioGenRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ApiItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ColItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ReqItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.TableItem;
import com.sk.skala.axcalibur.feature.scenario.dto.response.item.ScenarioListResponse;
import com.sk.skala.axcalibur.feature.scenario.entity.ApiListEntity;
import com.sk.skala.axcalibur.feature.scenario.entity.DbDesignEntity;
import com.sk.skala.axcalibur.feature.scenario.entity.ProjectEntity;
import com.sk.skala.axcalibur.feature.scenario.entity.RequestEntity;
import com.sk.skala.axcalibur.feature.scenario.entity.ScenarioEntity;
import com.sk.skala.axcalibur.feature.scenario.repository.ApiListRepository;
import com.sk.skala.axcalibur.feature.scenario.repository.DbDesignRepository;
import com.sk.skala.axcalibur.feature.scenario.repository.ProjectRepository;
import com.sk.skala.axcalibur.feature.scenario.repository.RequestRepository;
import com.sk.skala.axcalibur.feature.scenario.repository.ScenarioRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioGenServiceImpl implements ScenarioGenService {

    private final ProjectRepository projectRepository;
    private final RequestRepository requestRepository;
    private final ApiListRepository apiListRepository;
    private final DbDesignRepository dbDesignRepository;
    private final ScenarioRepository scenarioRepository;
    private final ObjectMapper objectMapper;

    @Override
    public ScenarioGenRequestDto prepareRequestData(Integer projectKey) {
        log.info("시나리오 생성 요청 데이터 준비 시작 - projectKey: {}", projectKey);
        
        try {
            // 프로젝트 존재 확인
            ProjectEntity project = projectRepository.findByKey(projectKey)
                .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 프로젝트입니다.", ErrorCode.PROJECT_NOT_FOUND));

            // 요구사항 정보 수집
            List<ReqItem> requirements = collectRequirements(projectKey);
            
            // API 정보 수집  
            List<ApiItem> apiList = collectApiList(projectKey);
            
            // 테이블 정보 수집
            List<TableItem> tableList = collectTableList(projectKey);
            
            log.info("요청 데이터 수집 완료 - 요구사항: {}개, API: {}개, 테이블: {}개", 
                requirements.size(), apiList.size(), tableList.size());
            
            return ScenarioGenRequestDto.builder()
                .requirement(requirements)
                .apiList(apiList)
                .tableList(tableList)
                .build();
                
        } catch (Exception e) {
            log.error("요청 데이터 준비 실패: {}", e.getMessage());
            throw new BusinessExceptionHandler("요청 데이터 준비 실패: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public List<ScenarioListResponse> parseAndSaveScenarios(String fastApiResponse, Integer projectKey) {
        log.info("FastAPI 응답 파싱 및 DB 저장 시작 - projectKey: {}", projectKey);
        
        try {
            // FastAPI 응답 JSON 파싱
            JsonNode jsonNode = objectMapper.readTree(fastApiResponse);
            JsonNode scenarioListNode = jsonNode.get("scenarioList");
            
            if (scenarioListNode == null || !scenarioListNode.isArray()) {
                throw new BusinessExceptionHandler("잘못된 시나리오 응답 형식입니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            }
            
            // 프로젝트 엔티티 조회 (수정된 메서드명)
            ProjectEntity project = projectRepository.findByKey(projectKey)
                .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 프로젝트입니다.", ErrorCode.PROJECT_NOT_FOUND));
            
            List<ScenarioListResponse> responseList = new ArrayList<>();
            
            // 각 시나리오를 DB에 저장
            for (JsonNode scenarioNode : scenarioListNode) {
                ScenarioEntity entity = ScenarioEntity.builder()
                    .id(scenarioNode.get("id").asText())
                    .name(scenarioNode.get("name").asText())
                    .description(scenarioNode.get("description") != null ? scenarioNode.get("description").asText() : null)
                    .flow_chart(scenarioNode.get("flowChart") != null ? scenarioNode.get("flowChart").asText() : null)
                    .projectKey(project)
                    .build();
                
                ScenarioEntity saved = scenarioRepository.save(entity);
                log.info("시나리오 저장 완료 - scenarioKey: {}, scenarioId: {}", saved.getKey(), saved.getId());
                
                // 응답 DTO로 변환
                responseList.add(ScenarioListResponse.builder()
                    .id(saved.getId())
                    .name(saved.getName())
                    .build());
            }
            
            log.info("총 {}개 시나리오 저장 완료", responseList.size());
            return responseList;
            
        } catch (Exception e) {
            log.error("시나리오 파싱 및 저장 실패: {}", e.getMessage());
            throw new BusinessExceptionHandler("시나리오 저장 실패: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 요구사항 정보 수집
    private List<ReqItem> collectRequirements(Integer projectKey) {
        log.debug("요구사항 정보 수집 중...");
        
        List<RequestEntity> requestEntities = requestRepository.findByProjectKey(projectKey);
        
        return requestEntities.stream()
            .map(entity -> ReqItem.builder()
                .id(entity.getId())
                .name(entity.getName())
                .desc(entity.getDescription())
                .priority(entity.getPriorityKey().getName())
                .major(entity.getMajorKey().getName())
                .middle(entity.getMiddleKey().getName())
                .minor(entity.getMinorKey().getName())
                .build())
            .collect(Collectors.toList());
    }

    // API 정보 수집 (파라미터 없이 간단히)
    private List<ApiItem> collectApiList(Integer projectKey) {
        log.debug("API 정보 수집 중...");
        
        List<ApiListEntity> apiEntities = apiListRepository.findByProjectKey(projectKey);
        
        return apiEntities.stream()
            .map(entity -> ApiItem.builder()
                .id(entity.getId())
                .name(entity.getName())
                .desc(entity.getDescription())
                .method(entity.getMethod())
                .url(entity.getUrl())
                .path(entity.getPath())
                .reqId(entity.getRequestKey().getId())
                // 파라미터는 빈 리스트로 설정 (ParameterRepository 없음)
                .pathQuery(new ArrayList<>())
                .request(new ArrayList<>())
                .response(new ArrayList<>())
                .build())
            .collect(Collectors.toList());
    }

    // 테이블 정보 수집
    @Override
    public List<TableItem> collectTableList(Integer projectKey) {
        log.debug("테이블 정보 수집 중...");
        
        List<DbDesignEntity> dbDesignEntities = dbDesignRepository.findByProjectKeyWithColumns(projectKey);
        
        return dbDesignEntities.stream()
            .map(entity -> {
                List<ColItem> columns = entity.getColumns().stream()
                    .map(col -> ColItem.builder()
                        .name(col.getCol_name())
                        .colName(col.getCol_name())
                        .desc(col.getDescription())
                        .type(col.getType())
                        .length(col.getLength())
                        .isPk(col.getIsPk())
                        .fk(col.getFk())
                        .isNull(col.getIsNull())
                        .constraint(col.getConstraintType())
                        .build())
                    .collect(Collectors.toList());
                
                return TableItem.builder()
                    .name(entity.getName())
                    .column(columns)
                    .build();
            })
            .collect(Collectors.toList());
    }
}