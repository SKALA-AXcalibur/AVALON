package com.sk.skala.axcalibur.spec.feature.spec.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String projectId);
}
