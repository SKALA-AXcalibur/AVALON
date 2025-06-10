package com.sk.skala.axcalibur.spec.feature.spec.service;

import org.springframework.boot.autoconfigure.rsocket.RSocketProperties.Server.Spec;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;

import com.sk.skala.axcalibur.spec.feature.spec.entity.ProjectEntity;
import com.sk.skala.axcalibur.spec.feature.spec.entity.SpecFileEntity;
import com.sk.skala.axcalibur.spec.feature.spec.repository.SpecFileRepository;
import com.sk.skala.axcalibur.spec.global.code.ErrorCode;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;

import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.spec.feature.spec.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

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
    
    @Transactional
    @Override
    public void saveToDatabase(String fileName, String projectId, String savedPath, int fileTypeKey) {
        
        ProjectEntity project = findByProjectId(projectId);
        final String oldPathToDelete;

        // db 커밋 후 파일 삭제
        try {
            Optional<SpecFileEntity> existingSpecFileOptional = specFileRepository.findByProjectAndFileTypeKey(project, fileTypeKey);

            SpecFileEntity specFileToPersist; // db에 저장할 엔티티

            if (existingSpecFileOptional.isPresent()) {
                specFileToPersist = existingSpecFileOptional.get();
                oldPathToDelete = specFileToPersist.getPath(); // 기존 파일 경로 저장
                
                // 기존 엔티티 path와 name 업데이트
                specFileToPersist.setPath(savedPath);
                specFileToPersist.setName(fileName);
                log.info("기존 메타데이터 업데이트: PjtId={}, 유형={}, 기존 경로: {}, 새 경로: {}", projectId, fileTypeKey, oldPathToDelete, savedPath);
            } else {
                // 새로운 메타데이터 저장
                specFileToPersist = SpecFileEntity.builder()
                    .path(savedPath)
                    .name(fileName)
                    .fileTypeKey(fileTypeKey)
                    .project(project) // 프로젝트 엔티티 설정
                    .build();
                oldPathToDelete = null; // 삭제할 경로 없음

                log.info("새 메타데이터 저장: PjtId={}, 유형={}, 경로: {}", projectId, fileTypeKey, savedPath);
            }
            // 메타데이터 저장 + 업데이트
            specFileRepository.save(specFileToPersist);

            // 트랜잭션 커밋 후 파일 삭제
            if (oldPathToDelete != null) {
                if (TransactionSynchronizationManager.isSynchronizationActive()) {
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            fileStorageService.deleteFileByPath(oldPathToDelete);
                            log.info("커밋 후 기존 파일 삭제 완료: {}", oldPathToDelete);
                        } catch (Exception ex) {
                            log.error("커밋 후 파일 삭제 실패: {}", oldPathToDelete, ex);
                        }
                    }
                });
            }
        }
        } catch (Exception e) {
            // 다른 종류의 예외 (DB 연결 문제 등) 처리
            log.error("메타데이터 저장 실패: PjtId={}", projectId, e);
            throw new BusinessExceptionHandler("메타데이터 저장에 실패", ErrorCode.DATABASE_OPERATION_FAILED);
        } 
    }

    @Override
    @Transactional
    public void deleteMetadata(String projectId) {

        ProjectEntity project = findByProjectId(projectId);
            
        try {
            specFileRepository.deleteAllByProject(project);
            log.info("메타데이터 삭제 완료: PjtId={}", projectId);
        } catch (Exception e) {
            log.error("메타데이터 삭제 실패: PjtId={}", projectId, e);
            throw new BusinessExceptionHandler("명세서 메타정보 삭제 실패", ErrorCode.DATABASE_OPERATION_FAILED);
        }
    }

    private ProjectEntity findByProjectId(String projectId) {
        return projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 프로젝트입니다.", ErrorCode.PROJECT_NOT_FOUND));
    }

}

