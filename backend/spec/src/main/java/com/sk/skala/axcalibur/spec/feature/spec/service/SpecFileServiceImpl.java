package com.sk.skala.axcalibur.spec.feature.spec.service;

import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.spec.feature.spec.entity.ProjectEntity;
import com.sk.skala.axcalibur.spec.feature.spec.entity.SpecFileEntity;
import com.sk.skala.axcalibur.spec.feature.spec.repository.SpecFileRepository;
import com.sk.skala.axcalibur.spec.feature.spec.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpecFileServiceImpl implements SpecFileService {

    private final SpecFileRepository specFileRepository;
    private final ProjectRepository ProjectRepository;

    @Override
    public void saveToDatabase(String fileName, String projectId, String savedPath, int fileTypeKey) {
        
        ProjectEntity project = ProjectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));


        SpecFileEntity entity = SpecFileEntity.builder()
                .path(savedPath)
                .name(fileName)
                .fileTypeKey(fileTypeKey)
                .project(project) // 프로젝트 엔티티 설정
                .build();

        specFileRepository.save(entity);
    }

}
