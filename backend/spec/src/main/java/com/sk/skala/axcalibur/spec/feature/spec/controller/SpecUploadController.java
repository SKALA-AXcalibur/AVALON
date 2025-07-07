package com.sk.skala.axcalibur.spec.feature.spec.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sk.skala.axcalibur.spec.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;

/**
 * 명세서 업로드 컨트롤러 인터페이스
 * '명세서 업로드(IF-SP-0001)' 파트를 구현합니다.
 * @param key 인증용 Cookie 문자열
 * @param requirementFile 요구사항 정의서 파일
 * @param interfaceDef 인터페이스 정의서 파일
 * @param interfaceDesign 인터페이스 설계서 파일
 * @param databaseDesign 테이블 설계서 파일
 * @return 업로드 결과 전달
 */
public interface SpecUploadController {

    @Operation(summary = "명세서 업로드", description = "요구사항정의서, 인터페이스정의서, 설계서, 테이블설계서를 업로드합니다.")
    ResponseEntity<SuccessResponse<List<Object>>> uploadSpec(
        @CookieValue("avalon") String key,
        @RequestParam MultipartFile requirementFile,
        @RequestParam MultipartFile interfaceDef,
        @RequestParam MultipartFile interfaceDesign,
        @RequestParam MultipartFile databaseDesign
    );
}
