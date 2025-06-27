package com.sk.skala.axcalibur.feature.testcase.controller;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.axcalibur.feature.testcase.dto.request.TcUpdateRequest;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcDetailResponse;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcListResponse;
import com.sk.skala.axcalibur.feature.testcase.repository.TestCaseRepository;
import com.sk.skala.axcalibur.feature.testcase.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.feature.testcase.service.TcCommandService;
import com.sk.skala.axcalibur.feature.testcase.service.TcQueryService;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/tc/v1")
@RequiredArgsConstructor
@Slf4j
public class TcManageControllerImpl implements TcManageController {
    private final ProjectIdResolverService projectIdResolverService;
    private final TestCaseRepository testcaseRepository;

    private final TcQueryService testcaseQueryService;
    private final TcCommandService testcaseCommandService;

    // 시나리오 별 TC 조회
    @Override
    @GetMapping("/scenario/{scenarioId}")
    public ResponseEntity<SuccessResponse<TcListResponse>> getTestcaseLists(
        @PathVariable String scenarioId,
        @CookieValue("avalon") String key,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "10") int query
    ) {
        // Redis에서 cookie 키 인증 (예외 발생 시 Global handler에서 처리)
        projectIdResolverService.resolveProjectId(key);

        // 테스트케이스 조회
        List<String> all = testcaseRepository.findAllByScenarioId(scenarioId);
        List<String> sliced = all.stream()
                                 .skip(offset)
                                 .limit(query)
                                 .toList();

        TcListResponse response = new TcListResponse(all.size(), sliced);

        return ResponseEntity.ok(
            SuccessResponse.<TcListResponse>builder()
                .data(response)
                .status(SuccessCode.SELECT_SUCCESS)
                .message(SuccessCode.SELECT_SUCCESS.getMessage())
                .build()
        );
    };

    // TC ID를 통한 특정 TC 조회
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

        return ResponseEntity.ok(
            SuccessResponse.<TcDetailResponse>builder()
                .data(response)
                .status(SuccessCode.SELECT_SUCCESS)
                .message(SuccessCode.SELECT_SUCCESS.getMessage())
                .build()
        );
    }
    
    // 특정 TC 삭제
    @Override
    @DeleteMapping("/{tcId}")
    public ResponseEntity<SuccessResponse<Void>> deleteTestcase(
        @PathVariable String tcId,
        @CookieValue("avalon") String key
    ) {
        // Redis에서 cookie 키 인증 (예외 발생 시 Global handler에서 처리)
        Integer projectId = projectIdResolverService.resolveProjectId(key);

        testcaseCommandService.deleteTestcase(tcId, projectId);
        
        // 응답시간 헤더에 반환
        HttpHeaders headers = new HttpHeaders();
        headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        // 정상 처리 응답(data는 null)
        return ResponseEntity
        .status(SuccessCode.DELETE_SUCCESS.getStatus())
        .headers(headers)
        .body(SuccessResponse.<Void>builder()
            .data(null)  // 빈 객체 반환
            .status(SuccessCode.DELETE_SUCCESS)
            .message(SuccessCode.DELETE_SUCCESS.getMessage())
            .build());
    }

    // 특정 TC 내용 update
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

        // 응답시간 헤더에 반환
        HttpHeaders headers = new HttpHeaders();
        headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        // 정상 처리 응답(data는 null)
        return ResponseEntity
        .status(SuccessCode.UPDATE_SUCCESS.getStatus())
        .headers(headers)
        .body(SuccessResponse.<Void>builder()
            .data(null)  // 빈 객체 반환
            .status(SuccessCode.UPDATE_SUCCESS)
            .message(SuccessCode.UPDATE_SUCCESS.getMessage())
            .build());
    }
}
