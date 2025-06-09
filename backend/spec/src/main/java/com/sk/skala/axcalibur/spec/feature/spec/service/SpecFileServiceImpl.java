package com.sk.skala.axcalibur.spec.feature.spec.service;

import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.spec.feature.spec.entity.ProjectEntity;
import com.sk.skala.axcalibur.spec.feature.spec.entity.SpecFileEntity;
import com.sk.skala.axcalibur.spec.feature.spec.repository.SpecFileRepository;
import com.sk.skala.axcalibur.spec.global.code.ErrorCode;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;

import jakarta.transaction.Transactional;

import com.sk.skala.axcalibur.spec.feature.spec.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

/**
 * 명세서 파일의 메타데이터를 데이터베이스에 저장
 * 파일 이름, 경로, 파일 유형, 프로젝트 정보 등을 저장
 * 프로젝트 ID를 통해 ProjectEntity를 조회한 뒤, 연관관계로 설정
 */
@Service
@RequiredArgsConstructor
public class SpecFileServiceImpl implements SpecFileService {

    private final SpecFileRepository specFileRepository;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public void saveToDatabase(String fileName, String projectId, String savedPath, int fileTypeKey) {
        
        ProjectEntity project = projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        SpecFileEntity entity = SpecFileEntity.builder()
                .path(savedPath)
                .name(fileName)
                .fileTypeKey(fileTypeKey)
                .project(project) // 프로젝트 엔티티 설정
                .build();
        
        try {
            specFileRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("메타정보 저장 실패", e);
        }

   
    }

    @Override
    public void deleteMetadata(String projectId) {

        ProjectEntity project = projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));
            
        try {
            specFileRepository.deleteAllByProject(project);
        } catch (Exception e) {
            throw new RuntimeException("메타정보 삭제 실패", e);
        }
    }

}
