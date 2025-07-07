package com.sk.skala.axcalibur.spec.feature.spec.service;

import com.sk.skala.axcalibur.spec.feature.spec.dto.ProjectContext;

/**
 * 명세서 파일의 메타데이터를 데이터베이스에 저장
 * 파일 이름, 경로, 파일 유형, 프로젝트 정보 등을 저장
 * 프로젝트 ID를 통해 ProjectEntity를 조회한 뒤, 연관관계로 설정
 */
public interface SpecFileService {
    void saveToDatabase(String fileName, ProjectContext projectContext, String savedPath, int fileTypeKey);
    void deleteMetadata(ProjectContext projectContext);
}
