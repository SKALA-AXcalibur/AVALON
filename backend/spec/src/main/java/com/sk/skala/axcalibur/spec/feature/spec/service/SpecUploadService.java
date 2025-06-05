package com.sk.skala.axcalibur.spec.feature.spec.service;

import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.spec.feature.spec.dto.request.SpecUploadRequest;
import com.sk.skala.axcalibur.spec.feature.spec.dto.response.SpecUploadResponse;

/**
 * 명세서 업로드 로직 구현
 * - PVC 저장 파트에 넘겨줌 -> 이후 DB까지 저장된 파일 정보 받아 응답으로 반환
 */
@Service
public interface SpecUploadService {
    // Controller에서 받아온 project ID와 request body 받아오는 함수
    public SpecUploadResponse uploadFiles(String projectIdStr, SpecUploadRequest request);
}