package com.sk.skala.axcalibur.spec.feature.spec.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.spec.feature.spec.entity.ProjectEntity;
import com.sk.skala.axcalibur.spec.feature.spec.entity.SpecFileEntity;
import com.sk.skala.axcalibur.spec.feature.spec.repository.SpecFileRepository;
import com.sk.skala.axcalibur.spec.global.code.ErrorCode;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;

import jakarta.transaction.Transactional;

import com.sk.skala.axcalibur.spec.feature.spec.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * 명세서 파일의 메타데이터를 데이터베이스에 저장
 * 파일 이름, 경로, 파일 유형, 프로젝트 정보 등을 저장
 * 프로젝트 ID를 통해 ProjectEntity를 조회한 뒤, 연관관계로 설정
 * 동일한 프로젝트 및 파일 유형 조합의 파일이 존재할 경우 기존 파일을 덮어씀
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpecFileServiceImpl implements SpecFileService {

    private final SpecFileRepository specFileRepository;
    private final ProjectRepository projectRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public void saveToDatabase(String fileName, String projectId, String savedPath, int fileTypeKey) {
        
        ProjectEntity project = findByProjectId(projectId);

        SpecFileEntity entity = SpecFileEntity.builder()
                .path(savedPath)
                .name(fileName)
                .fileTypeKey(fileTypeKey)
                .project(project) // 프로젝트 엔티티 설정
                .build();
        
        try {
            specFileRepository.save(entity);
            log.info("메타데이터 저장 성공");

        } catch (DataIntegrityViolationException e) {
            // DataIntegrityViolationException이 발생하면, 중복 키가 있다는 의미
            log.warn("메타데이터 중복 감지: PjtId={}, 유형={}. 덮어쓰기 시도.", projectId, fileTypeKey, e);

            // 기존 SpecFileEntity를 찾음
            Optional<SpecFileEntity> existingSpecFileOptional = specFileRepository.findByProjectAndFileTypeKey(project, fileTypeKey);

            if (existingSpecFileOptional.isPresent()) {
                SpecFileEntity existingSpecFile = existingSpecFileOptional.get();

                // 기존 메타데이터가 가리키는 파일을 삭제
                try {
                    fileStorageService.deleteFileByPath(existingSpecFile.getPath()); 
                    log.info("기존 파일 삭제 완료: {}", existingSpecFile.getPath());
                } catch (BusinessExceptionHandler fileEx) {
                    // 파일 삭제 중 발생
                    log.error("기존 파일 삭제 실패 (PjtId={}, 파일경로={}): {}", projectId, existingSpecFile.getPath(), fileEx.getMessage(), fileEx);
                    throw new BusinessExceptionHandler("기존 물리 파일 삭제 실패: " + fileEx.getMessage(), ErrorCode.FILE_DELETE_FAILED);
                } catch (Exception fileEx) {
                    // 예상치 못한 물리 파일 삭제 오류 (예: IOException)
                    log.error("기존 파일 삭제 중 예상치 못한 오류 (PjtId={}, 파일경로={}): {}", projectId, existingSpecFile.getPath(), fileEx.getMessage(), fileEx);
                    throw new BusinessExceptionHandler("기존 파일 삭제 중 예상치 못한 오류 발생.", ErrorCode.FILE_DELETE_FAILED);
                }
                
                // 기존 데이터베이스 메타데이터 삭제
                specFileRepository.delete(existingSpecFile);
                log.info("기존 메타데이터 삭제 완료: PjtId={}, 유형={}", projectId, fileTypeKey);

                // 다시 저장
                specFileRepository.save(entity);
                log.info("메타데이터 덮어쓰기 성공: PjtId={}, 파일={}", projectId, fileTypeKey, fileName);

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
