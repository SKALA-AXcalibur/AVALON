package com.sk.skala.axcalibur.spec.feature.spec.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import com.sk.skala.axcalibur.spec.feature.spec.dto.response.SpecUploadResponse;
import com.sk.skala.axcalibur.spec.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;

/**
 * 명세서 업로드 컨트롤러 인터페이스
 * '명세서 업로드(IF-SP-0001)' 파트를 구현합니다.
 * @param request HttpServletRequest 객체
 * @param requirementFile 요구사항 정의서 파일
 * @param interfaceDef 인터페이스 정의서 파일
 * @param interfaceDesign 인터페이스 설계서 파일
 * @return 업로드 결과를 담은 ResponseEntity 객체
 */
public interface SpecUploadController {

    @Operation(summary = "명세서 업로드", description = "요구사항정의서, 인터페이스정의서, 설계서를 업로드합니다.")
    public ResponseEntity<SuccessResponse<SpecUploadResponse>> uploadSpec(
        HttpServletRequest request,
        @RequestParam MultipartFile requirementFile,
        @RequestParam MultipartFile interfaceDef,
        @RequestParam MultipartFile interfaceDesign
    );
}
