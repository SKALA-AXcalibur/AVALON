package com.sk.skala.axcalibur.spec.feature.spec.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.sk.skala.axcalibur.spec.feature.spec.dto.request.SpecUploadRequest;
import com.sk.skala.axcalibur.spec.feature.spec.dto.response.SpecUploadResponse;
import com.sk.skala.axcalibur.spec.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;

/**
 * 명세서 업로드 컨트롤러 인터페이스
 * '명세서 업로드(IF-SP-0001)' 파트를 구현합니다.
 */
public interface SpecUploadController {

    @Operation(summary = "명세서 업로드", description = "요구사항정의서, 인터페이스정의서, 설계서를 업로드합니다.")
    public ResponseEntity<SuccessResponse<SpecUploadResponse>> uploadSpec(
        @PathVariable String projectId,
        @RequestBody SpecUploadRequest request
    );
}
