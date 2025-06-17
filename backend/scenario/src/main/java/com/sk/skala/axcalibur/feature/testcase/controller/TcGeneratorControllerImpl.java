package com.sk.skala.axcalibur.feature.testcase.controller;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.axcalibur.feature.testcase.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;

/**
 * 테스트케이스 생성 요청 인터페이스
 * '테스트케이스 생성 요청(IF-TC-0001)'을 실제 구현합니다.
 */
@RestController
@RequestMapping("/tc/v1")
@RequiredArgsConstructor
public class TcGeneratorControllerImpl implements TcGeneratorContoller {
    private final ProjectIdResolverService projectIdResolverService;

    @Override
    @PostMapping
    public ResponseEntity<SuccessResponse<List<Object>>> generateTestCases(@CookieValue("avalon") String key) {
        // Redis에서 projectId 가져오기 (예외 발생 시 Global handler에서 처리)
        Integer projectId = projectIdResolverService.resolveProjectId(key);

        // 이후 로직 구현
        // project ID로 필요 DB 조회 -> fastAPI 측에 필요한 생성 정보 + 생성 요청 전달
        
        // 응답시간 헤더에 반환
        HttpHeaders headers = new HttpHeaders();
        headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        // 정상 처리 응답(data는 null)
        return ResponseEntity
        .status(SuccessCode.INSERT_SUCCESS.getStatus())
        .headers(headers)
        .body(SuccessResponse.<List<Object>>builder()
            .data(Collections.emptyList())  // 빈 리스트 반환
            .status(SuccessCode.INSERT_SUCCESS)
            .message(SuccessCode.INSERT_SUCCESS.getMessage())
            .build());
    }
}
