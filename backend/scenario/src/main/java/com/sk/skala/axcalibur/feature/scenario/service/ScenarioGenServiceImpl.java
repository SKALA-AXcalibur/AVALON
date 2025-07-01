package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioGenRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ApiItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ReqItem;
import com.sk.skala.axcalibur.feature.scenario.dto.response.item.ScenarioItem;            
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
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioGenServiceImpl implements ScenarioGenService {

    private final ProjectRepository projectRepository;
    private final RequestRepository requestRepository;
    private final ApiListRepository apiListRepository;
    private final ScenarioRepository scenarioRepository;
    

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
    public List<ScenarioEntity> parseAndSaveScenarios(List<ScenarioItem> scenarioList, Integer projectKey) {
        // 시나리오 저장
       
        ProjectEntity project = projectRepository.findById(projectKey)
            .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 프로젝트입니다.", ErrorCode.PROJECT_NOT_FOUND));

        try {
            // 프로젝트 내 기존 시나리오 id 목록을 조회
            int maxNo = scenarioRepository.findMaxScenarioNoByProjectKey(projectKey); // 기존 시나리오 id 중 최대 번호
            
            List<ScenarioEntity> savedEntities = new ArrayList<>();

            // 각 시나리오를 DB에 저장
            for (int i = 0; i < scenarioList.size(); i++) {
                ScenarioItem scenarioItem = scenarioList.get(i);
                String newScenarioId = String.format("scenario-%03d", maxNo + 1 + i); // 각각 다른 ID
                
                // DB 엔티티로 매핑
                ScenarioEntity entity = ScenarioEntity.builder()
                    .scenarioId(newScenarioId)
                    .name(scenarioItem.getTitle())
                    .description(scenarioItem.getDescription())
                    .validation(scenarioItem.getValidation())
                    .flowChart(null)
                    .project(project)
                    .build();

                savedEntities.add(scenarioRepository.save(entity));
            }
            
            // 모든 시나리오 저장 완료 후 반환
            return savedEntities;
            
        } catch (DataIntegrityViolationException e) {
            // 시나리오 ID 중복 등 데이터 무결성 위반
            log.error("시나리오 저장 중 데이터 무결성 위반 - 프로젝트: {}, 에러: {}", projectKey, e.getMessage());
            throw new BusinessExceptionHandler("시나리오 ID가 중복되었습니다. 다시 시도해주세요.", ErrorCode.INTERNAL_SERVER_ERROR);
        } 
    }

    @Override
    public List<ReqItem> collectRequirements(Integer projectKey) {
        // 요구사항 정보 수집
        List<RequestEntity> requestEntities = requestRepository.findByProjectKey_Id(projectKey);
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
        List<ApiListEntity> apiEntities = apiListRepository.findByProjectKey_Id(projectKey);
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
}