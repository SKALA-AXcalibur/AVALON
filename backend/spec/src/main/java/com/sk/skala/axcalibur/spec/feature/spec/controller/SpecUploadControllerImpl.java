package com.sk.skala.axcalibur.spec.feature.spec.controller;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sk.skala.axcalibur.spec.feature.spec.dto.request.SpecUploadRequest;
import com.sk.skala.axcalibur.spec.feature.spec.dto.response.SpecUploadResponse;
import com.sk.skala.axcalibur.spec.feature.spec.service.SpecUploadService;
import com.sk.skala.axcalibur.spec.feature.spec.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.spec.global.code.SuccessCode;
import com.sk.skala.axcalibur.spec.global.response.SuccessResponse;

/**
 * 명세서 업로드 컨트롤러
 * - '명세서 업로드(IF-SP-0001)' 파트 실제 구현부
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
    public ResponseEntity<SuccessResponse<SpecUploadResponse>> uploadSpec(
        HttpServletRequest request,
        @RequestParam MultipartFile requirementFile,
        @RequestParam MultipartFile interfaceDef,
        @RequestParam MultipartFile interfaceDesign) {
        
        // specUploadRequest dto 객체 생성
        SpecUploadRequest specUploadRequest = new SpecUploadRequest(requirementFile, interfaceDef, interfaceDesign);

        // Redis에서 projectId 가져오기 (예외 발생 시 Global handler에서 처리)
        String projectId = projectIdResolverService.resolveProjectId(request);
        
        // 서비스 호출
        SpecUploadResponse response = specUploadService.uploadFiles(projectId, specUploadRequest);

        // 정상 처리 
        return ResponseEntity
            .status(SuccessCode.INSERT_SUCCESS.getStatus())
            .body(SuccessResponse.<SpecUploadResponse>builder()
                .data(response)
                .status(SuccessCode.INSERT_SUCCESS)
                .message(SuccessCode.INSERT_SUCCESS.getMessage())
                .build());
    }
} 
