package com.sk.skala.axcalibur.feature.testcase.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.axcalibur.feature.testcase.dto.request.TcUpdateRequest;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcDetailResponse;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcListResponse;
import com.sk.skala.axcalibur.feature.testcase.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.feature.testcase.service.TcCommandService;
import com.sk.skala.axcalibur.feature.testcase.service.TcQueryService;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.repository.TestCaseRepository;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;


/**
 * 테스트케이스 관련 조회/삭제/수정 인터페이스의 실제 구현부
 * - 시나리오 ID를 입력받아 해당 시나리오로부터 생성된 TC 리스트를 조회하는 파트를 구현합니다.
 * - TC ID를 입력받아 조회/수정/삭제를 구현합니다.
 * - 시나리오 별 TC 목록 조회(IF-TC-0003)
 * - TC 상세 정보 조회(IF-TC-0004)
 * - TC 수정(IF-TC-0005)
 * - TC 삭제(IF-TC-0006)
 * - TC 추가(IF-TC-0010)
 */
@RestController
@RequestMapping("/tc/v1")
@RequiredArgsConstructor
@Slf4j
public class TcManageControllerImpl implements TcManageController {
    private final ProjectIdResolverService projectIdResolverService;
    private final TestCaseRepository testcaseRepository;

    private final TcQueryService testcaseQueryService;
    private final TcCommandService testcaseCommandService;

    // IF-TC-0003: 시나리오 별 TC 조회
    @Override
    @GetMapping("/scenario/{scenarioId}")
    public ResponseEntity<SuccessResponse<TcListResponse>> getTestcaseLists(
        @PathVariable String scenarioId,
        @CookieValue("avalon") String key,
        Pageable pageable
    ) {
        // Redis에서 cookie 키 인증 (예외 발생 시 Global handler에서 처리)
        projectIdResolverService.resolveProjectId(key);

        // 테스트케이스 조회
        Page<String> paged = testcaseRepository.findAllByScenarioId(scenarioId, pageable);

        TcListResponse response = new TcListResponse((int) paged.getTotalElements(), paged.getContent());
        
        return ResponseEntity
            .status(SuccessCode.SELECT_SUCCESS.getStatus())
            .body(SuccessResponse.<TcListResponse>builder()
                .data(response)
                .status(SuccessCode.SELECT_SUCCESS)
                .message(SuccessCode.SELECT_SUCCESS.getMessage())
                .build());
    };

    // IF-TC-0004: TC ID를 통한 특정 TC 조회
    @Override
    @GetMapping("/{tcId}")
    public ResponseEntity<SuccessResponse<TcDetailResponse>> getTestcases(
        @PathVariable String tcId,
        @CookieValue("avalon") String key
    ) {
        // Redis에서 cookie 키 인증 (예외 발생 시 Global handler에서 처리)
        projectIdResolverService.resolveProjectId(key);

        // 서비스 호출
        TcDetailResponse response = testcaseQueryService.getTestcaseDetail(tcId);

        return ResponseEntity
            .status(SuccessCode.SELECT_SUCCESS.getStatus())
            .body(SuccessResponse.<TcDetailResponse>builder()
                .data(response)
                .status(SuccessCode.SELECT_SUCCESS)
                .message(SuccessCode.SELECT_SUCCESS.getMessage())
                .build());
    }
    
    // IF-TC-0005: 특정 TC 내용 update
    @Override
    @PutMapping("/{tcId}")
    public ResponseEntity<SuccessResponse<Void>> updateTestcase(
        @PathVariable String tcId,
        @CookieValue("avalon") String key,
        @RequestBody TcUpdateRequest request
    ) { 
        // Redis에서 cookie 키 인증 (예외 발생 시 Global handler에서 처리)
        Integer projectId = projectIdResolverService.resolveProjectId(key);
        testcaseCommandService.updateTestcase(tcId, projectId, request);

        // 정상 처리 응답(data는 null)
        return ResponseEntity
        .status(SuccessCode.UPDATE_SUCCESS.getStatus())
        .body(SuccessResponse.<Void>builder()
            .data(null)  // 빈 객체 반환
            .status(SuccessCode.UPDATE_SUCCESS)
            .message(SuccessCode.UPDATE_SUCCESS.getMessage())
            .build());
    }

    // IF-TC-0006: 특정 TC 삭제
    @Override
    @DeleteMapping("/{tcId}")
    public ResponseEntity<SuccessResponse<Void>> deleteTestcase(
        @PathVariable String tcId,
        @CookieValue("avalon") String key
    ) {
        // Redis에서 cookie 키 인증 (예외 발생 시 Global handler에서 처리)
        Integer projectId = projectIdResolverService.resolveProjectId(key);

        testcaseCommandService.deleteTestcase(tcId, projectId);

        // 정상 처리 응답(data는 null)
        return ResponseEntity
        .status(SuccessCode.DELETE_SUCCESS.getStatus())
        .body(SuccessResponse.<Void>builder()
            .data(null)  // 빈 객체 반환
            .status(SuccessCode.DELETE_SUCCESS)
            .message(SuccessCode.DELETE_SUCCESS.getMessage())
            .build());
    }

    // IF-TC-0010: TC 추가
    @Override
    @PostMapping("/api/{scenarioId}/{apiId}")
    public ResponseEntity<SuccessResponse<String>> addTestcase(
        @PathVariable String scenarioId,
        @PathVariable String apiId, 
        @CookieValue("avalon") String key,
        @RequestBody TcUpdateRequest request
    ) {
        projectIdResolverService.resolveProjectId(key);
        
        String tcId = testcaseCommandService.addTestcase(scenarioId, apiId, request);
        
        // 정상 처리 응답
        return ResponseEntity
        .status(SuccessCode.INSERT_SUCCESS.getStatus())
        .body(SuccessResponse.<String>builder()
            .data(tcId)
            .status(SuccessCode.INSERT_SUCCESS)
            .message(SuccessCode.INSERT_SUCCESS.getMessage())
            .build());
    }
}
