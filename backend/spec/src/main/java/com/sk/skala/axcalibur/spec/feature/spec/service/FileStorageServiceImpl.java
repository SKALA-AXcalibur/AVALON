package com.sk.skala.axcalibur.spec.feature.spec.service;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sk.skala.axcalibur.spec.global.code.ErrorCode;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 파일 저장 기능
 * 프로젝트 ID를 기준으로 파일을 저장
 * 삭제 시 프로젝트 폴더 내 모든 파일을 삭제하고, 폴더 자체도 삭제
 */
@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.basepath}")
    private String basepath; // 추후 pvc 경로로 변경 필요

    @Override
    public String storeFile(MultipartFile file, String projectId) {

        String originalFilename = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());

        if (originalFilename.contains("..")) {
            log.warn("경로 조작 시도 가능성이 있는 파일 이름입니다: {}", originalFilename);
            throw new BusinessExceptionHandler("유효하지 않은 파일 이름입니다.",
                ErrorCode.INVALID_FILE_NAME);
}
     
        // 디렉토리 경로 생성
        String relativePath = projectId + File.separator + originalFilename;
        File targetFile = new File(basepath + relativePath);

        // 상위 디렉토리 생성
        File parentDir = targetFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            log.error("상위 디렉토리 생성 실패: {}", parentDir.getAbsolutePath());
            throw new BusinessExceptionHandler("파일 저장 경로 생성에 실패했습니다.", ErrorCode.FILE_STORAGE_ERROR);
        }

        try {
            file.transferTo(targetFile);
            log.info("파일 저장 성공: {}", targetFile.getAbsolutePath());
        } catch (IOException | IllegalStateException e) {
            log.error("파일 저장 실패: {} (프로젝트 ID: {})", targetFile.getAbsolutePath(), projectId, e);
            throw new BusinessExceptionHandler("파일 저장에 실패했습니다.", ErrorCode.FILE_STORAGE_ERROR);
        }
        return relativePath;
    }

    /**
     * 프로젝트 ID를 기준으로 해당 프로젝트 폴더 내 모든 파일을 삭제하고, 폴더 자체도 삭제
     * 업로드 실패 시 호출됨
     */
    @Override
    public void deleteFile(String projectId) {
        String projectDirPath = basepath + projectId + File.separator;
        File projectDir = new File(projectDirPath);

        try {
            // 프로젝트 폴더가 존재하지 않는 경우 조기 종료
            if (!projectDir.exists()) {
                log.info("삭제하려는 프로젝트 폴더가 존재하지 않습니다: {}", projectDirPath);
                return; 
            }

            // 폴더 내 모든 파일 삭제
            File[] files = projectDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.delete()) {
                        log.error("파일 삭제 실패: {}", file.getAbsolutePath());
                        throw new BusinessExceptionHandler("파일 삭제 실패: " + file.getName(), ErrorCode.FILE_DELETE_FAILED);
                    }
                }
                log.info("프로젝트 폴더 내 모든 파일 삭제 완료: {}", projectDirPath);
            } 

            // 폴더 자체 삭제
            if (projectDir.delete()) {
                log.info("프로젝트 폴더 삭제 성공: {}", projectDirPath);
            } else {
                log.error("프로젝트 폴더 삭제 실패: {}", projectDirPath);
                throw new BusinessExceptionHandler("프로젝트 폴더 삭제에 실패했습니다.", ErrorCode.FILE_DELETE_FAILED);
            }
        } catch (Exception e) { // 예상치 못한 오류
            log.error("프로젝트 폴더 삭제 중 예상치 못한 오류 발생: {}", projectDirPath, e);
            throw new BusinessExceptionHandler("프로젝트 폴더 삭제 중 예상치 못한 오류 발생.", ErrorCode.FILE_DELETE_FAILED);
        }
    }

    /**
     * filepath를 기준으로 단일 파일 삭제
     * filePath는 basepath 제외한 상대경로
     * 파일 덮어씌울떄 호출됨
     */
    @Override 
    public void deleteFileByPath(String filePath) {
        File fileToDelete = new File(basepath + filePath);

        if (!fileToDelete.exists()) {
            log.info("삭제하려는 파일이 존재하지 않습니다: {}", fileToDelete.getAbsolutePath());
            return;
        }

        if (fileToDelete.delete()) {
            log.info("파일 삭제 성공: {}", fileToDelete.getAbsolutePath());
        } else {
            log.error("파일 삭제 실패: {}", fileToDelete.getAbsolutePath());
            throw new BusinessExceptionHandler("파일 삭제 실패: " + fileToDelete.getName(), ErrorCode.FILE_DELETE_FAILED);
        }
    }
}


