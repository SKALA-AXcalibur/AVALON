package com.sk.skala.axcalibur.feature.scenario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioCreateRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioUpdateRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioCreateResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioDeleteResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioDetailResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioListDto;
import com.sk.skala.axcalibur.global.response.SuccessResponse;



/**
 * 시나리오 컨트롤러 인터페이스
 * - 시나리오 목록 조회(IF-SN-0009)
 * - 시나리오 생성(IF-SN-0003)
 * - 시나리오 수정(IF-SN-0004)
 * - 시나리오 삭제(IF-SN-0007)
 * - 시나리오 상세 조회(IF-SN-0008)
 */
public interface ScenarioController {
    
    /**
     * 프로젝트별 시나리오 목록 조회
     * @param key 프로젝트 인증 쿠키
     * @return 시나리오 목록 응답
     */
    @GetMapping("/scenario/v1/project")
    ResponseEntity<SuccessResponse<ScenarioListDto>> getScenarioList(
        @CookieValue("avalon") String key);
    
    /**
     * 시나리오 추가
     * @param key 프로젝트 인증 쿠키
     * @param requestDto 시나리오 추가 DTO
     * @return 시나리오 추가 응답
     */
    @PostMapping("/scenario/v1")
    ResponseEntity<SuccessResponse<ScenarioCreateResponseDto>> createScenario(
        @CookieValue("avalon") String key,
        @RequestBody ScenarioCreateRequestDto requestDto
    );
    
    /**
     * 시나리오 수정
     * @param key 프로젝트 인증 쿠키
     * @param scenarioId 수정할 시나리오 ID
     * @param requestDto 시나리오 수정 요청 DTO
     * @return 성공 응답
     */
    @PutMapping("/scenario/v1/{scenarioId}")
    ResponseEntity<SuccessResponse<Void>> updateScenario(
        @CookieValue("avalon") String key,
        @PathVariable("scenarioId") String scenarioId,
        @RequestBody ScenarioUpdateRequestDto requestDto
    );
    
    /**
     * 시나리오 삭제
     * @param key 프로젝트 인증 쿠키
     * @param id 삭제할 시나리오 ID
     * @return 삭제 응답
     */
    @DeleteMapping("/scenario/v1/scenario/{id}")
    ResponseEntity<SuccessResponse<ScenarioDeleteResponseDto>> deleteScenario(
        @CookieValue("avalon") String key,
        @PathVariable("id") String id
    );
    
    /**
     * 시나리오 상세 조회
     * @param key 프로젝트 인증 쿠키
     * @param id 조회할 시나리오 ID
     * @return 시나리오 상세 정보
     */
    @GetMapping("/scenario/v1/scenario/{id}")
    ResponseEntity<SuccessResponse<ScenarioDetailResponseDto>> getScenarioDetail(
        @CookieValue("avalon") String key,
        @PathVariable("id") String id
    );
} 