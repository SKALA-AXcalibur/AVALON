package com.sk.skala.axcalibur.spec.feature.spec.controller;

import lombok.RequiredArgsConstructor;


import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sk.skala.axcalibur.spec.feature.spec.dto.ProjectContext;
import com.sk.skala.axcalibur.spec.feature.spec.dto.request.SpecUploadRequest;
import com.sk.skala.axcalibur.spec.feature.spec.service.SpecUploadService;
import com.sk.skala.axcalibur.spec.feature.spec.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.spec.global.code.SuccessCode;
import com.sk.skala.axcalibur.spec.global.response.SuccessResponse;

/**
 * 명세서 업로드 컨트롤러
 * - '명세서 업로드(IF-SP-0001)' 파트 실제 구현부
 * @param key 인증용 Cookie 문자열
 * @param requirementFile 요구사항 정의서 파일
 * @param interfaceDef 인터페이스 정의서 파일
 * @param interfaceDesign 인터페이스 설계서 파일
 * @return 업로드 결과 반환
 * - 요청 데이터는 유효성 검사를 거쳐 서비스에 전달
 * - 예외 발생 시 global.exception.GlobalExceptionHandler 에서 일괄 처리 
 */
@RestController
@RequestMapping("/spec/v1")
@RequiredArgsConstructor
public class SpecUploadControllerImpl implements SpecUploadController {
    // 서비스 주입
    private final SpecUploadService specUploadService;
    private final ProjectIdResolverService projectIdResolverService;

    @Override
    @PostMapping
    public ResponseEntity<SuccessResponse<List<Object>>> uploadSpec(
        @CookieValue("avalon") String key,
        @RequestParam MultipartFile requirementFile,
        @RequestParam MultipartFile interfaceDef,
        @RequestParam MultipartFile interfaceDesign) {
        
        // specUploadRequest dto 객체 생성
        SpecUploadRequest specUploadRequest = new SpecUploadRequest(requirementFile, interfaceDef, interfaceDesign);

        // Redis에서 projectId 가져오기 (예외 발생 시 Global handler에서 처리)
        ProjectContext project = projectIdResolverService.resolveProjectId(key);

        // 서비스 호출
        specUploadService.uploadFiles(project, specUploadRequest);

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