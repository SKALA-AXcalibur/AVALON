package com.sk.skala.axcalibur.spec.feature.spec.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sk.skala.axcalibur.spec.feature.spec.dto.request.SpecUploadRequest;
import com.sk.skala.axcalibur.spec.feature.spec.dto.response.SpecUploadResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 명세서 업로드 실제 구현
 * - 명세서 파일명/확장자 단순 파싱
 * - PVC 저장 파트에 넘겨줌 -> 이후 DB까지 저장된 파일 정보 받아 응답으로 반환
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpecUploadServiceImpl implements SpecUploadService {
    private final FileStorageService fileStorageService;
    private final SpecFileService specFileService;
    
    // Controller에서 받아온 project ID와 request body 받아오는 함수
    @Override
    public SpecUploadResponse uploadFiles(String projectId, SpecUploadRequest request) {
        // 저장이 완료된 파일 명 반환
        List<String> resultNames = new ArrayList<>();

        // 파일-문서 유형 매핑 정의
        Map<MultipartFile, Integer> fileTypeMap = Map.of(
            request.getRequirementFile(), 1,
            request.getInterfaceDef(), 2,
            request.getInterfaceDesign(), 3
        );

        for (Map.Entry<MultipartFile, Integer> entry : fileTypeMap.entrySet()) {
            MultipartFile file = entry.getKey();
            Integer type = entry.getValue();
    
            if (file != null && !file.isEmpty()) {
                try {
                    String savedPath = fileStorageService.storeFile(file, projectId);
                    String fileName = file.getOriginalFilename();
                    specFileService.saveToDatabase(fileName, projectId, savedPath, type);
                    resultNames.add(file.getOriginalFilename());
                } catch (Exception e) {
                    log.error("문서 유형 {} 저장 실패: {}", type, e.getMessage());
                    // throw new RuntimeException("문서 저장 실패: " + e.getMessage(), e);
                }
            }
        }
    
        return SpecUploadResponse.builder()
            .uploadResults(resultNames)
            .build();
    }
}
