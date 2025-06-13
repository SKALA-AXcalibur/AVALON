package com.sk.skala.axcalibur.spec.feature.spec.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.spec.feature.spec.dto.ProjectContext;
import com.sk.skala.axcalibur.spec.feature.spec.entity.FileTypeEntity;
import com.sk.skala.axcalibur.spec.feature.spec.entity.ProjectEntity;
import com.sk.skala.axcalibur.spec.feature.spec.entity.SpecFileEntity;
import com.sk.skala.axcalibur.spec.feature.spec.repository.SpecFileRepository;
import com.sk.skala.axcalibur.spec.global.code.ErrorCode;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.spec.feature.spec.repository.FileTypeRepository;
import com.sk.skala.axcalibur.spec.feature.spec.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 명세서 파일의 메타데이터를 데이터베이스에 저장
 * 파일 이름, 경로, 파일 유형, 프로젝트 정보 등을 저장
 * 프로젝트 ID를 통해 ProjectEntity를 조회한 뒤, 연관관계로 설정
 * 동일한 프로젝트 및 파일 유형 조합의 파일이 존재할 경우 기존 파일을 덮어씀
 * 트랜잭션 커밋 후 기존 파일 삭제
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpecFileServiceImpl implements SpecFileService {

    private final SpecFileRepository specFileRepository;
    private final ProjectRepository projectRepository;
    private final FileStorageService fileStorageService;
    private final FileTypeRepository fileTypeRepository;
    
    @Transactional
    @Override
    public void saveToDatabase(String fileName, ProjectContext projectContext, String savedPath, int fileType) {
    
        // key 기반 project 조회
        ProjectEntity project = projectRepository.findById(projectContext.getKey())
            .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 프로젝트입니다.", ErrorCode.PROJECT_NOT_FOUND));
        String projectId = projectContext.getProjectId();

        FileTypeEntity fileTypeEntity = fileTypeRepository.findById(fileType).orElseThrow(() -> new BusinessExceptionHandler("파일 유형이 존재하지 않습니다.", ErrorCode.FILE_STORAGE_ERROR));

        // 삭제할 기존 파일 경로 리스트
        List<String> oldPathsToDelete = new ArrayList<>();
        
        
        // 기존 파일(같은 타입, 같은 프로젝트) 찾아서 경로 백업
        Optional<SpecFileEntity> existingSpecFileOptional = specFileRepository.findByProjectAndFileType(project, fileTypeEntity);

        // db 커밋 후 파일 삭제
        if (existingSpecFileOptional.isPresent()) {
            SpecFileEntity existing = existingSpecFileOptional.get();
            String oldPath = existing.getPath();
            // 경로가 다를 때만 삭제 목록에 추가
            if (!oldPath.equals(savedPath)) {
                oldPathsToDelete.add(oldPath);
            }
            existing.updateFileInfo(savedPath, fileName); // path, name만 갱신
            specFileRepository.save(existing); // update (PK 유지)
            log.info("기존 메타데이터 업데이트: PjtId={}, 유형={}, 기존 경로: {}, 새 경로: {}", projectId, fileType, oldPath, savedPath);
        } else {
            SpecFileEntity newEntity = SpecFileEntity.builder()
                    .path(savedPath)
                    .name(fileName)
                    .fileType(fileTypeEntity)
                    .project(project)
                    .build();
            specFileRepository.save(newEntity);
            log.info("새 메타데이터 저장: PjtId={}, 유형={}, 경로: {}", projectId, fileType, savedPath);
        }

        // 트랜잭션 커밋 후 파일 삭제 (리스트 전체 순회)
        if (!oldPathsToDelete.isEmpty()) {
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                final List<String> pathsToDelete = new ArrayList<>(oldPathsToDelete);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        for (String pathToDelete : pathsToDelete) {
                            try {
                                fileStorageService.deleteFileByPath(pathToDelete);
                                log.info("커밋 후 기존 파일 삭제 완료: {}", pathToDelete);
                            } catch (Exception ex) {
                                log.error("커밋 후 파일 삭제 실패: {}", pathToDelete, ex);
                            }
                        }
                    }
                });
            }
        }
  
    }

    @Override
    @Transactional
    public void deleteMetadata(ProjectContext projectContext) {

        ProjectEntity project = projectRepository.findById(projectContext.getKey())
            .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 프로젝트입니다.", ErrorCode.PROJECT_NOT_FOUND)); 
        String projectId = projectContext.getProjectId();

        specFileRepository.deleteAllByProject(project);
        log.info("메타데이터 삭제 완료: PjtId={}", projectId); 
          
    }
}


