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
import com.sk.skala.axcalibur.global.entity.RequestEntity;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ApiListEntity;
import com.sk.skala.axcalibur.global.entity.ProjectEntity;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.ApiListRepository;
import com.sk.skala.axcalibur.global.repository.ProjectRepository;
import com.sk.skala.axcalibur.global.repository.RequestRepository;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;
import com.sk.skala.axcalibur.global.repository.MappingRepository;

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
    private final MappingRepository mappingRepository;
    

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
        // null 체크 추가
        if (scenarioList == null || scenarioList.isEmpty()) {
            log.warn("시나리오 리스트가 null이거나 비어있습니다. 프로젝트 키: {}", projectKey);
            throw new BusinessExceptionHandler("시나리오 리스트가 비어있습니다.", ErrorCode.NOT_VALID_ERROR);
        }
        
        // 시나리오 저장
       
        ProjectEntity project = projectRepository.findById(projectKey)
            .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 프로젝트입니다.", ErrorCode.PROJECT_NOT_FOUND));

        try {
            // 프로젝트 내 기존 시나리오 id 목록을 조회
            int maxNo = scenarioRepository.findMaxScenarioNoByProjectKey(projectKey); // 기존 시나리오 id 중 최대 번호
            
            List<ScenarioEntity> entitiesToSave = new ArrayList<>();

            // 각 시나리오 엔티티 생성
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

                entitiesToSave.add(entity);
            }
            
            return scenarioRepository.saveAll(entitiesToSave);
            
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
                .name(entity.getName())
                .desc(entity.getDescription())
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
                .build())
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public long deleteScenariosByProjectKey(Integer projectKey) {
        try {
            // 프로젝트 키를 기준으로 시나리오 조회
            List<ScenarioEntity> scenariosToDelete = scenarioRepository.findByProject_Id(projectKey);
            long deletedCount = scenariosToDelete.size();
            
            if (!scenariosToDelete.isEmpty()) {
                // 1. 시나리오 ID들 수집
                List<Integer> scenarioIds = scenariosToDelete.stream()
                    .map(ScenarioEntity::getId)
                    .collect(Collectors.toList());
                
                // 2. 매핑 데이터를 한 번에 삭제
                mappingRepository.deleteAllByScenarioKey(scenarioIds);
                
                // 3. 시나리오 데이터 삭제
                scenarioRepository.deleteAllById(scenarioIds);
            }
            
            return deletedCount;
            
        } catch (Exception e) {
            log.error("시나리오 삭제 실패 - 프로젝트: {}, 에러: {}", projectKey, e.getMessage());
            throw new BusinessExceptionHandler("시나리오 데이터 삭제 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}