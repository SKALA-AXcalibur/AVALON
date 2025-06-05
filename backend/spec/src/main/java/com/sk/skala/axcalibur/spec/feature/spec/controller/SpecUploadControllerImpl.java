package com.sk.skala.axcalibur.spec.feature.spec.controller;

// import java.util.List;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.axcalibur.spec.feature.spec.dto.request.SpecUploadRequest;
import com.sk.skala.axcalibur.spec.feature.spec.dto.response.SpecUploadResponse;

/**
 * 명세서 업로드 컨트롤러
 * '명세서 업로드(IF-SP-0001)' 파트 실제 구현부
 */
@RestController
@RequestMapping("/api/spec/v1")
public class SpecUploadControllerImpl implements SpecUploadController {
    
    @Override
    @PostMapping("/{projectId}")
    public ResponseEntity<SpecUploadResponse> uploadSpec(
        @PathVariable String projectId,
        @Valid @RequestBody SpecUploadRequest request) {
        
        SpecUploadResponse response;
        // 로직 구현

        return ResponseEntity.ok(response);
    }
}
