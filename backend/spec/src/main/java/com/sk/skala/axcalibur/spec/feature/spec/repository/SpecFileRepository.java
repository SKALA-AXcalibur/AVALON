package com.sk.skala.axcalibur.spec.feature.spec.repository;

import com.sk.skala.axcalibur.spec.feature.spec.entity.ProjectEntity;
import com.sk.skala.axcalibur.spec.feature.spec.entity.SpecFileEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 프로젝트와 연관된 모든 명세서 파일 레코드를 제거
 * 프로젝트와 파일 유형 키를 이용한 특정 명세서 파일 조회
 */
@Repository
public interface SpecFileRepository extends JpaRepository<SpecFileEntity, Integer> {
    void deleteAllByProject(ProjectEntity project);
    Optional<SpecFileEntity> findByProjectAndFileTypeKey(ProjectEntity project, int fileTypeKey);
}