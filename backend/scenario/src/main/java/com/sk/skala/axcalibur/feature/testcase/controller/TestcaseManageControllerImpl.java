package com.sk.skala.axcalibur.feature.testcase.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.axcalibur.feature.testcase.dto.response.TestcaseDetailResponse;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TestcaseListResponse;
import com.sk.skala.axcalibur.feature.testcase.repository.TestCaseRepository;
import com.sk.skala.axcalibur.feature.testcase.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.feature.testcase.service.TestcaseQueryService;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/tc/v1")
@RequiredArgsConstructor
@Slf4j
public class TestcaseManageControllerImpl implements TestcaseManageController {
    private final ProjectIdResolverService projectIdResolverService;
    private final TestCaseRepository testcaseRepository;

    private final TestcaseQueryService testcaseQueryService;

    // 시나리오 별 TC 조회
    @GetMapping("/scenario/{scenarioId}")
    public ResponseEntity<SuccessResponse<TestcaseListResponse>> getTestcaseLists(
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

        TestcaseListResponse response = new TestcaseListResponse(all.size(), sliced);

        return ResponseEntity.ok(
            SuccessResponse.<TestcaseListResponse>builder()
                .data(response)
                .status(SuccessCode.SELECT_SUCCESS)
                .message(SuccessCode.SELECT_SUCCESS.getMessage())
                .build()
        );
    };

}
