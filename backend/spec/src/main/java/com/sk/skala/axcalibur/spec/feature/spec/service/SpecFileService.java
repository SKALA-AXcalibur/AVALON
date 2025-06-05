package com.sk.skala.axcalibur.spec.feature.spec.service;

public interface SpecFileService {
    void saveToDatabase(String fileName, String projectId, String savedPath, int fileTypeKey);
}
