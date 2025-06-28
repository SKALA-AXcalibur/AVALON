package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.scenario.client.ScenarioFlowClient;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioFlowDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioFlowRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioGenRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ApiItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ReqItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ScenarioFlowApi;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioListResponse;
import com.sk.skala.axcalibur.feature.scenario.entity.RequestEntity;
import com.sk.skala.axcalibur.feature.scenario.repository.RequestRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ApiListEntity;
import com.sk.skala.axcalibur.global.entity.ProjectEntity;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.ApiListRepository;
import com.sk.skala.axcalibur.global.repository.ProjectRepository;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시나리오 생성 서비스 구현
 * - 요구사항 및 API 정보 수집
 * - 시나리오 저장
 * - 새로운 시나리오 ID 생성
 * - 시나리오 흐름도 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioGenServiceImpl implements ScenarioGenService {

    private final ProjectRepository projectRepository;
    private final RequestRepository requestRepository;
    private final ApiListRepository apiListRepository;
    private final ScenarioRepository scenarioRepository;
    private final ScenarioFlowClient scenarioFlowClient;

    @Override
    public ScenarioGenRequestDto prepareRequestData(Integer projectKey) {
        // 요구사항 및 API 정보, 프로젝트 ID 수집
        List<ReqItem> requirements = collectRequirements(projectKey); // 요구사항 정보 수집
        List<ApiItem> apiList = collectApiList(projectKey); // API 정보 수집
        String projectId = projectRepository.findById(projectKey) // 프로젝트 ID 조회
            .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 프로젝트입니다.", ErrorCode.PROJECT_NOT_FOUND))
            .getProjectId();
        
        return ScenarioGenRequestDto.builder()
            .projectId(projectId)
            .requirement(requirements)
            .apiList(apiList)
            .build();         
   
    }

    @Override
    @Transactional
    public List<ScenarioListResponse> parseAndSaveScenarios(List<ScenarioListResponse> scenarioList, Integer projectKey) {
        // 시나리오 저장
        try {
            ProjectEntity project = projectRepository.findById(projectKey)
                .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 프로젝트입니다.", ErrorCode.PROJECT_NOT_FOUND));
            List<ScenarioListResponse> responseList = new ArrayList<>();

            // 각 시나리오를 DB에 저장
            for (ScenarioListResponse scenario : scenarioList) {
                // 새로운 시나리오 ID 생성
                String newScenarioId = generateNewScenarioId();

                // DB 엔티티로 매핑
                ScenarioEntity entity = ScenarioEntity.builder()
                    .scenarioId(newScenarioId)
                    .name(scenario.getName())
                    .description("") // 필요시 채우기
                    .validation("") // 필요시 채우기
                    .flowChart(null)
                    .project(project)
                    .build();

                scenarioRepository.save(entity);

                // 응답용 DTO로 변환
                responseList.add(ScenarioListResponse.builder()
                    .scenarioId(entity.getScenarioId())
                    .name(entity.getName())
                    .build());
            }
            return responseList;
        } catch (DataIntegrityViolationException e) {
            log.error("DB 무결성 위반", e);
            throw new BusinessExceptionHandler("DB 무결성 위반: " + e.getMessage(), ErrorCode.DATABASE_OPERATION_FAILED);
        } catch (NullPointerException e) {
            log.error("NullPointer 발생", e);
            throw new BusinessExceptionHandler("Null 값 오류: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("시나리오 파싱 및 저장 실패", e);
            throw new BusinessExceptionHandler("시나리오 저장 실패: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<ReqItem> collectRequirements(Integer projectKey) {
        // 요구사항 정보 수집
        List<RequestEntity> requestEntities = requestRepository.findByProjectKey(projectKey);
        return requestEntities.stream()
            .map(entity -> ReqItem.builder()
                .reqId(entity.getRequestId())
                .name(entity.getName())
                .desc(entity.getDescription())
                .priority(entity.getPriorityKey().getName())
                .major(entity.getMajorKey().getName())
                .middle(entity.getMiddleKey().getName())
                .minor(entity.getMinorKey().getName())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public List<ApiItem> collectApiList(Integer projectKey) {
        // API 정보 수집 
        List<ApiListEntity> apiEntities = apiListRepository.findByProjectKey(projectKey);
        return apiEntities.stream()
            .map(entity -> ApiItem.builder()
                .apiId(entity.getApiListId())
                .name(entity.getName())
                .desc(entity.getDescription())
                .method(entity.getMethod())
                .path(entity.getPath())
                .reqId(entity.getId().toString())
                .build())
            .collect(Collectors.toList());
    }
   
    @Override
    public String generateNewScenarioId() {
        // 새로운 시나리오 ID 생성
        // DB에서 현재 최대 시나리오 ID를 조회해서 +1
        String maxId = scenarioRepository.findMaxScenarioId(); 
        int nextNum = 1;
        if (maxId != null && maxId.startsWith("scenario-")) {
            try {
                nextNum = Integer.parseInt(maxId.substring(9)) + 1;
            } catch (NumberFormatException e) {
                log.warn("시나리오 ID 숫자 변환 실패: {}", maxId);
                // 무시하고 1로 둠
            }
        }
        return String.format("scenario-%03d", nextNum);
    }

    @Override
    public ScenarioFlowRequestDto prepareFlowRequestData(List<ScenarioListResponse> savedScenarios, Integer projectKey) {
        try {
            
            // 저장된 시나리오들을 ScenarioFlowDto로 변환
            List<ScenarioFlowDto> scenarioFlowList = savedScenarios.stream()
                .map(scenario -> {
                    // 각 시나리오에 대한 API 정보 수집 (실제로는 시나리오별 API 매핑이 필요할 수 있음)
                    List<ApiListEntity> apiEntities = apiListRepository.findByProjectKey(projectKey);
                    List<ScenarioFlowApi> apiList = apiEntities.stream()
                        .map(api -> new ScenarioFlowApi(
                            api.getApiListId(),
                            api.getName(),
                            api.getDescription()
                        ))
                        .collect(Collectors.toList());
                    
                    return ScenarioFlowDto.builder()
                        .id(scenario.getScenarioId())
                        .description(scenario.getName())
                        .apiList(apiList)
                        .build();
                })
                .collect(Collectors.toList());
            
            return ScenarioFlowRequestDto.builder()
                .scenarioList(scenarioFlowList)
                .build();
                
        } catch (Exception e) {
            log.error("시나리오 흐름도 요청 데이터 준비 실패", e);
            throw new BusinessExceptionHandler("시나리오 흐름도 요청 데이터 준비 실패: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void generateAndUpdateFlowChart(List<ScenarioListResponse> savedScenarios, Integer projectKey) {
        try {
            // 흐름도 생성 요청 데이터 준비
            ScenarioFlowRequestDto flowRequestDto = prepareFlowRequestData(savedScenarios, projectKey);
            
            // FastAPI로 흐름도 생성 요청 (DB 저장은 FastAPI에서 처리)
            scenarioFlowClient.sendInfoAndGetResponse(flowRequestDto);
            
        } catch (Exception e) {
            log.error("시나리오 흐름도 생성 실패", e);
            throw new BusinessExceptionHandler("시나리오 흐름도 생성 실패: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}