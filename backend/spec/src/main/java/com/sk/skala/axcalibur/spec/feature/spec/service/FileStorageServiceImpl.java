package com.sk.skala.axcalibur.spec.feature.spec.service;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final String basepath = "";

    @Override
    public String storeFile(MultipartFile file, String projectId) {
        String relativePath = "project_" + projectId + "/" + file.getOriginalFilename();

        try {
            String dirPath = basepath + "project_" + projectId + "/";
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
