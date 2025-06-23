// ScenarioGenControllerImpl.java
package com.sk.skala.axcalibur.feature.scenario.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioGenRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioGenResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.item.ScenarioListResponse;
import com.sk.skala.axcalibur.feature.scenario.client.ScenarioGenClient;
import com.sk.skala.axcalibur.feature.scenario.service.ScenarioGenService;
import com.sk.skala.axcalibur.feature.scenario.repository.AvalonCookieRepository;
import com.sk.skala.axcalibur.feature.scenario.entity.AvalonCookieEntity;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

@Slf4j
@RestController
@RequestMapping("/api/scenario/v1/create")
@RequiredArgsConstructor
public class ScenarioGenControllerImpl implements ScenarioGenController {

    private final ScenarioGenService scenarioGenService;
    private final ScenarioGenClient scenarioGenClient;
    private final AvalonCookieRepository avalonCookieRepository;

    @Override
    @PostMapping
    public ResponseEntity<SuccessResponse<ScenarioGenResponseDto>> generateScenario(
        @CookieValue("avalon") String avalonCookie) {
        
        log.info("시나리오 생성 요청 시작 - cookie: {}", avalonCookie);
        
        try {
            // 쿠키에서 프로젝트 키 조회
            Integer projectKey = resolveProjectKeyFromCookie(avalonCookie);
            log.info("프로젝트 키 조회 완료 - projectKey: {}", projectKey);
            
            // DB에서 프로젝트 관련 정보 수집하여 FastAPI 요청 데이터 준비
            ScenarioGenRequestDto requestDto = scenarioGenService.prepareRequestData(projectKey);
            log.info("FastAPI 요청 데이터 준비 완료");
            
            // FastAPI로 시나리오 생성 요청 전송하고 응답 받기
            String fastApiResponse = scenarioGenClient.sendInfoAndGetResponse(projectKey.toString(), requestDto);
            log.info("FastAPI 시나리오 생성 완료");
            
            // FastAPI 응답을 파싱해서 DB에 저장하고 응답 DTO 생성
            List<ScenarioListResponse> scenarios = scenarioGenService.parseAndSaveScenarios(fastApiResponse, projectKey);
            log.info("시나리오 DB 저장 완료 - 생성된 시나리오 수: {}", scenarios.size());
            
            // 최종 응답 DTO 구성
            ScenarioGenResponseDto responseDto = ScenarioGenResponseDto.builder()
                .scenarioList(scenarios)
                .total(scenarios.size())
                .build();
            
            // 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            
            log.info("시나리오 생성 요청 완료 - 총 {}개 시나리오 생성", scenarios.size());
            
            // 성공 응답 반환
            return ResponseEntity
                .status(SuccessCode.INSERT_SUCCESS.getStatus())
                .headers(headers)
                .body(SuccessResponse.<ScenarioGenResponseDto>builder()
                    .data(responseDto)
                    .status(SuccessCode.INSERT_SUCCESS)
                    .message("시나리오 생성 및 저장 완료")
                    .build());
                    
        } catch (BusinessExceptionHandler e) {
            log.error("비즈니스 예외 발생: {}", e.getMessage());
            throw e; // Global Exception Handler에서 처리
        } catch (Exception e) {
            log.error("시나리오 생성 중 예상치 못한 오류 발생: {}", e.getMessage());
            throw new BusinessExceptionHandler(
                "시나리오 생성 중 오류가 발생했습니다: " + e.getMessage(), 
                ErrorCode.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    // 쿠키에서 프로젝트 키 조회
    private Integer resolveProjectKeyFromCookie(String avalonCookie) {
        log.debug("쿠키에서 프로젝트 키 조회 중...");
        
        AvalonCookieEntity cookieEntity = avalonCookieRepository.findByToken(avalonCookie)
            .orElseThrow(() -> new BusinessExceptionHandler(
                "유효하지 않은 인증 정보입니다.", 
                ErrorCode.NOT_FOUND_ERROR
            ));
        
        if (cookieEntity.getProjectKey() == null) {
            throw new BusinessExceptionHandler(
                "프로젝트 정보가 없습니다.", 
                ErrorCode.PROJECT_NOT_FOUND
            );
        }
        
        return cookieEntity.getProjectKey();
    }
}