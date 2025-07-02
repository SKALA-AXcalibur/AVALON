package com.sk.skala.axcalibur.feature.scenario.controller;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.axcalibur.feature.scenario.client.ScenarioGenClient;
import com.sk.skala.axcalibur.feature.scenario.dto.ProjectContext;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioGenRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioGenResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioListResponse;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.MappingResponseDto;
import com.sk.skala.axcalibur.feature.scenario.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.feature.scenario.service.ScenarioGenService;
import com.sk.skala.axcalibur.feature.scenario.service.MappingService;
import com.sk.skala.axcalibur.feature.scenario.service.ScenarioFlowService;

import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.entity.MappingEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.code.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import feign.FeignException;
import feign.RetryableException;

/**
 * 시나리오 생성 요청 인터페이스
 * '시나리오 생성 요청(IF-SN-0001)'
 * - 쿠키로부터 project ID를 불러와 시나리오 생성 요청을 진행
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ScenarioGenControllerImpl implements ScenarioGenController {

    private final ScenarioGenService scenarioGenService;
    private final ScenarioGenClient scenarioGenClient;
    private final ProjectIdResolverService projectIdResolverService;
    private final ScenarioFlowService scenarioFlowService;
    private final MappingService mappingService;

    @Override
    public ResponseEntity<ScenarioGenResponseDto> generateScenario(@CookieValue("avalon") String key) {
        try {
            // 1. 프로젝트 정보 수집
            ProjectContext projectContext = projectIdResolverService.resolveProjectId(key);
            Integer projectKey = projectContext.getKey();
            ScenarioGenRequestDto requestDto = scenarioGenService.prepareRequestData(projectKey);
            
            // 2. FastAPI 호출 
            ScenarioResponseDto response = scenarioGenClient.sendInfoAndGetResponse(requestDto);
            
            // 3. DB 저장
            List<ScenarioEntity> savedEntities = scenarioGenService.parseAndSaveScenarios(response.getScenarioList(), projectKey);

            // 4. 매핑표 생성
            mappingService.generateMappingForAllScenarios(projectKey);

            // 5. 플로우차트 생성
            scenarioFlowService.generateFlowchartForAllScenarios(projectKey);
 
            // ScenarioEntity를 ScenarioListResponse로 변환
            List<ScenarioListResponse> scenarioListResponse = savedEntities.stream()
                .map(entity -> ScenarioListResponse.builder()
                    .scenarioId(entity.getScenarioId())
                    .name(entity.getName())
                    .build())
                .collect(Collectors.toList());

            // 최종 응답 DTO 구성
            ScenarioGenResponseDto responseDto = ScenarioGenResponseDto.builder()
                .scenarioList(scenarioListResponse)
                .total(scenarioListResponse.size())
                .build();
            
            // 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            
            return ResponseEntity
                .status(SuccessCode.INSERT_SUCCESS.getStatus())
                .headers(headers)
                .body(responseDto);
            
        } catch (RetryableException e) {
            // 타임아웃 등 
            throw new BusinessExceptionHandler("시나리오 생성 시간이 초과되었습니다.", ErrorCode.GATEWAY_TIMEOUT_ERROR);
        } catch (FeignException.UnprocessableEntity e) {
            // 422 에러 -> 입력 데이터 문제
            throw new BusinessExceptionHandler("입력 데이터에 문제가 있습니다.", ErrorCode.NOT_VALID_ERROR);
        }
    }
}

