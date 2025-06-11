package com.sk.skala.axcalibur.feature.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.entity.FilePath;
import com.sk.skala.axcalibur.feature.entity.Project;

@Repository
public interface FilePathRepository extends JpaRepository<FilePath, Integer> {
    
    // 프로젝트별 파일 경로 목록 조회
    List<FilePath> findByProjectKey(Project projectKey);

    // 특정 경로로 조회
    List<FilePath> findByPath(String path);

    // 경로에 특정 문자열 포함된 것 조회
    List<FilePath> findByPathContaining(String path);

}
