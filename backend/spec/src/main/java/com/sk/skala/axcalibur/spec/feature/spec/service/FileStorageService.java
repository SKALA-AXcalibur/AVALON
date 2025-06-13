package com.sk.skala.axcalibur.spec.feature.spec.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 저장 기능
 * 프로젝트 ID를 기준으로 파일을 저장
 */
public interface FileStorageService {
    String storeFile(MultipartFile file, String projectId);
    void deleteDir(String projectId);
    void deleteFileByPath(String filePath);
}
