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
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ReqItem;
import com.sk.skala.axcalibur.feature.scenario.dto.response.item.ScenarioListResponse;
import com.sk.skala.axcalibur.global.entity.ApiListEntity;
import com.sk.skala.axcalibur.global.entity.ProjectEntity;
import com.sk.skala.axcalibur.feature.scenario.entity.RequestEntity;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.feature.scenario.repository.ApiListRepository;
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

            log.info("요청 데이터 수집 완료 - 요구사항: {}개, API: {}개", 
                requirements.size(), apiList.size());
            
            String projectId = project.getProjectId();
            log.info("프로젝트 ID 설정: {}", projectId);
            
            return ScenarioGenRequestDto.builder()
                .projectId(projectId)
                .requirement(requirements)
                .apiList(apiList)
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
            JsonNode scenarioListNode = jsonNode.get("scenario_list");
            
            if (scenarioListNode == null || !scenarioListNode.isArray()) {
                throw new BusinessExceptionHandler("잘못된 시나리오 응답 형식입니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            }
            
            // 프로젝트 엔티티 조회 (수정된 메서드명)
            ProjectEntity project = projectRepository.findByKey(projectKey)
                .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 프로젝트입니다.", ErrorCode.PROJECT_NOT_FOUND));
            
            List<ScenarioListResponse> responseList = new ArrayList<>();
            
            // 각 시나리오를 DB에 저장
            for (JsonNode scenarioNode : scenarioListNode) {
                // 1. 새로운 시나리오 ID 생성
                String newScenarioId = generateNewScenarioId();

                // 2. DB 엔티티로 매핑
                ScenarioEntity entity = ScenarioEntity.builder()
                    .scenarioId(newScenarioId) // Spring에서 생성
                    .name(scenarioNode.get("title").asText())
                    .description(scenarioNode.get("description").asText())
                    .validation(scenarioNode.get("validation").asText())
                    .flowChart(null) // 플로우차트는 추후
                    .project(project)
                    .build();

                scenarioRepository.save(entity);

                // 3. 응답용 DTO로 변환
                responseList.add(ScenarioListResponse.builder()
                    .id(entity.getScenarioId())
                    .name(entity.getName())
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
                .id(entity.getRequestId())
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
                .id(entity.getApiListId())
                .name(entity.getName())
                .desc(entity.getDescription())
                .method(entity.getMethod())
                .path(entity.getPath())
                .reqId(entity.getId().toString())
                .build())
            .collect(Collectors.toList());
    }

    private String generateNewScenarioId() {
        // DB에서 현재 최대 시나리오 ID를 조회해서 +1
        // 예시: scenario-001, scenario-002 ...
        String maxId = scenarioRepository.findMaxScenarioId(); // 이 메서드는 직접 구현 필요
        int nextNum = 1;
        if (maxId != null && maxId.startsWith("scenario-")) {
            try {
                nextNum = Integer.parseInt(maxId.substring(9)) + 1;
            } catch (NumberFormatException e) {
                // 무시하고 1로 둠
            }
        }
        return String.format("scenario-%03d", nextNum);
    }

}