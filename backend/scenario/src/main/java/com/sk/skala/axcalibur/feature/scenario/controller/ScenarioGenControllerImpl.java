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
import com.sk.skala.axcalibur.feature.scenario.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.feature.scenario.service.ScenarioGenService;
import com.sk.skala.axcalibur.feature.scenario.service.ScenarioMappingService;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
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
    private final ScenarioMappingService scenarioMappingService;

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
            
            // 4. 매핑/흐름도 생성 시도 (실패해도 시나리오 생성 응답은 정상 반환)
            try {
                log.info("시나리오 생성 완료 후 생성된 시나리오들에 대한 매핑/흐름도 생성 시작 - 프로젝트 키: {}, 시나리오 수: {}", projectKey, savedEntities.size());
                
                // 4-1. 생성된 시나리오들에 대한 매핑 생성
                scenarioMappingService.generateAndSaveMappingForScenarios(projectKey, savedEntities);
                log.info("시나리오 생성 후 매핑 생성 완료");
                
                // 4-2. 생성된 시나리오들에 대한 흐름도 생성
                scenarioMappingService.generateFlowchartForScenarios(projectKey, savedEntities);
                log.info("시나리오 생성 후 흐름도 생성 완료");
                
            } catch (Exception e) {
                log.error("매핑/흐름도 생성 실패했지만 시나리오 생성은 성공 - 응답 계속 진행: {}", e.getMessage(), e);
                // 매핑/흐름도 생성 실패 시에도 시나리오 생성 결과는 정상 반환
                // 예외를 다시 던지지 않음
            }
            
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
            
            log.info("시나리오 생성 및 응답 완료 - 생성된 시나리오 수: {}", scenarioListResponse.size());
            
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

