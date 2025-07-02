package com.sk.skala.axcalibur.spec.feature.spec.service;

import com.sk.skala.axcalibur.spec.feature.spec.dto.ProjectContext;
import com.sk.skala.axcalibur.spec.feature.spec.dto.request.SpecUploadRequest;

/**
 * 명세서 업로드 로직 구현
 * - PVC 저장 파트에 넘겨줌 -> 이후 DB까지 저장 결과 받아 응답으로 반환
 */
public interface SpecUploadService {
    // Controller에서 받아온 project dto와 request body 받아오는 함수
    void uploadFiles(ProjectContext project, SpecUploadRequest request);
}