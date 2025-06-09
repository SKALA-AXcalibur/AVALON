package com.sk.skala.axcalibur.spec.feature.spec.service;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 저장 기능
 * 프로젝트 ID를 기준으로 파일을 저장
 * 삭제 시 프로젝트 폴더 내 모든 파일을 삭제하고, 폴더 자체도 삭제
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.basepath}")
    private String basepath; // 추후 pvc 경로로 변경 필요

    @Override
    public String storeFile(MultipartFile file, String projectId) {
        String relativePath = projectId + "/" + file.getOriginalFilename(); // project_ 제거

        try {
            String dirPath = basepath + projectId + "/"; // project_ 제거
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            file.transferTo(new File(basepath + relativePath));  // 파일 저장

        } catch (IOException | IllegalStateException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        return relativePath;
    }

    @Override
    public void deleteFile(String projectId) {
        String projectDirPath = basepath + projectId + "/";
        File dir = new File(projectDirPath);
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
            dir.delete(); // 디렉토리 자체도 삭제
        }
    }
}
