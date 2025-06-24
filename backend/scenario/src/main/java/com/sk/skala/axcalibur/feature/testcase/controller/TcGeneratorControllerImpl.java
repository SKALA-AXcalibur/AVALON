package com.sk.skala.axcalibur.feature.testcase.controller;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.axcalibur.feature.testcase.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.feature.testcase.service.TcFacade;

import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 테스트케이스 생성 요청 인터페이스
 * '테스트케이스 생성 요청(IF-TC-0001)'을 실제 구현합니다.
 * - 쿠키로부터 project ID를 불러와 TC 생성 요청을 진행합니다.
 * - 생성 결과를 반환합니다.
 */
@RestController
@RequestMapping("/tc/v1")
@RequiredArgsConstructor
@Slf4j
public class TcGeneratorControllerImpl implements TcGeneratorController {
    private final ProjectIdResolverService projectIdResolverService;
    private final TcFacade tcFacade;

    @Override
    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> generateTestCases(@CookieValue("avalon") String key) {
        // Redis에서 projectId 가져오기 (예외 발생 시 Global handler에서 처리)
        Integer projectId = projectIdResolverService.resolveProjectId(key);

        // 테스트케이스 전체 생성 요청
        tcFacade.generateAllTestcases(projectId);

        // 응답시간 헤더에 반환
        HttpHeaders headers = new HttpHeaders();
        headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        // 정상 처리 응답(data는 null)
        return ResponseEntity
        .status(SuccessCode.INSERT_SUCCESS.getStatus())
        .headers(headers)
        .body(SuccessResponse.<Void>builder()
            .data(null)  // 빈 객체 반환
            .status(SuccessCode.INSERT_SUCCESS)
            .message(SuccessCode.INSERT_SUCCESS.getMessage())
            .build());
    }
}
