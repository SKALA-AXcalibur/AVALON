package com.sk.skala.axcalibur.spec.feature.spec.service;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.val;

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

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        return relativePath;
    }
}
