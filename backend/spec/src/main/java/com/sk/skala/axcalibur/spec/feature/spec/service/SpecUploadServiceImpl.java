package com.sk.skala.axcalibur.spec.feature.spec.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.spec.feature.spec.dto.ProjectContext;
import com.sk.skala.axcalibur.spec.feature.spec.dto.request.SpecUploadRequest;
import com.sk.skala.axcalibur.spec.feature.spec.entity.FileTypeEntity;
import com.sk.skala.axcalibur.spec.feature.spec.repository.FileTypeRepository;
import com.sk.skala.axcalibur.spec.global.code.ErrorCode;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 명세서 업로드 실제 구현
 * - 명세서 파일명/확장자 단순 파싱
 * - PVC 저장 파트에 넘겨줌 -> 이후 DB까지 저장 결과 받아 응답으로 반환
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpecUploadServiceImpl implements SpecUploadService {
    private final FileTypeRepository fileTypeRepository;
    
    private final FileStorageService fileStorageService;
    private final SpecFileService specFileService;
    
    // Controller에서 받아온 project dto와 request body 받아오는 함수
    @Override
    @Transactional
    public void uploadFiles(ProjectContext project, SpecUploadRequest request) {
        List<String> savedPaths = new ArrayList<>();        // 저장된 파일의 PVC 경로 list
        
        String projectId = project.getProjectId();

        Map<MultipartFile, FileTypeEntity> fileTypeMap = Map.of(
            request.getRequirementFile(), getFileTypeEntity(1),
            request.getInterfaceDef(), getFileTypeEntity(2),
            request.getInterfaceDesign(), getFileTypeEntity(3)
        );

        try {
            for (Map.Entry<MultipartFile, FileTypeEntity> entry : fileTypeMap.entrySet()) {
                MultipartFile file = entry.getKey();
                FileTypeEntity fileType = entry.getValue();
                Integer type = fileType.getId();

                if (file == null || file.isEmpty()) {
                    log.warn("파일이 비어 있거나 null입니다. 문서 유형: {}", type);
                    throw new BusinessExceptionHandler(ErrorCode.NOT_VALID_MULTIPART_ERROR);
                }

                String originalFilename = file.getOriginalFilename();

                String savedPath = fileStorageService.storeFile(file, projectId);
                specFileService.saveToDatabase(originalFilename, project, savedPath, type);

                savedPaths.add(savedPath);
            }

        } catch (BusinessExceptionHandler e) { // 비즈니스 로직에서 예외 발생 시
            log.error("업로드 실패 (비즈니스 예외) - {}", e.getMessage(), e);
            rollbackUploadedFiles(savedPaths);
            throw e; // 그대로 재던짐 (에러코드 보존)
        } catch (Exception e) {                // 그 외 예상하지 못한 부분에서 예외 발생 시
            log.error("업로드 실패 (알 수 없는 예외) - {}", e.getMessage(), e);
            rollbackUploadedFiles(savedPaths);
            throw new BusinessExceptionHandler("알 수 없는 오류", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 실패한 업로드 도중 저장된 파일들만 제거
    private void rollbackUploadedFiles(List<String> savedPaths) {
        for (String path : savedPaths) {
            try {
                fileStorageService.deleteFileByPath(path);
            } catch (BusinessExceptionHandler e) { // 삭제 실패 시 로그만 남기고 무시
                log.warn("파일 삭제 실패 (BusinessExceptionHandler 처리) [{}]: {}", path, e.getMessage());
            } catch (Exception e) {  
                log.warn("파일 삭제 중 예상치 못한 예외 발생 [{}]: {}", path, e.getMessage(), e);
            }
        }
    }

    // file_type DB에 정의된 유형별 id값과 매핑하는 코드
    private FileTypeEntity getFileTypeEntity(int id) {
        return fileTypeRepository.findById(id)
            .orElseThrow(() -> new BusinessExceptionHandler("파일 유형 없음", ErrorCode.INVALID_TYPE_VALUE));
    }
}
