package com.sk.skala.axcalibur.spec.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FileStorageService {

    // 현재 실행 디렉토리 기준으로 uploads 디렉토리 경로 설정
    // 향후 PVC 경로로 변경 예정
    private final String basePath = System.getProperty("user.dir") + "/uploads/";

    // 저장
    public String storeFile(MultipartFile file, Integer projectId, String fileName) {

        String projectPath = basePath + "project_" + projectId + "/";
        String fullPath = projectPath + fileName;

        try {
            File dir = new File(projectPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            file.transferTo(new File(fullPath)); 
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패" + fileName, e);
        }

        return fullPath;
    }

    // 조회
    public String getStoragePath(Integer projectId, String fileName) {
        return basePath + "project_" + projectId + "/" + fileName;
    }

    // 개별 파일 삭제
    public void deleteFile(String fullPath) {
        try {
            Files.deleteIfExists(Paths.get(fullPath));
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패: " + fullPath, e);
        }
    }

    // 프로젝트 전체 파일 삭제
    public void deleteProjectDirectory(Integer projectId) {
    String dirPath = basePath + "project_" + projectId + "/";
    File dir = new File(dirPath);

    if (dir.exists() && dir.isDirectory()) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete(); // 파일만 삭제
            }
        }
        dir.delete(); // 마지막에 디렉토리 삭제
    }
}
}


