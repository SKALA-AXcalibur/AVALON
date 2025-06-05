package com.sk.skala.axcalibur.spec.feature.spec.service;

import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.spec.feature.spec.entity.SpecFileEntity;
import com.sk.skala.axcalibur.spec.feature.spec.repository.SpecFileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpecFileServiceImpl implements SpecFileService {

    private final SpecFileRepository specFileRepository;

    @Override
    public void saveToDatabase(String fileName, String projectId, String savedPath, int fileTypeKey) {
        Integer parsedProjectId;
        try {
            parsedProjectId = Integer.parseInt(projectId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("유효하지 않은 프로젝트 ID 형식입니다: " + projectId, e);
        }

        SpecFileEntity entity = SpecFileEntity.builder()
                .path(savedPath)
                .projectKey(parsedProjectId)
                .name(fileName)
                .fileTypeKey(fileTypeKey)
                .build();

        specFileRepository.save(entity);
    }

}
