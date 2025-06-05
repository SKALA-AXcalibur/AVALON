package com.sk.skala.axcalibur.spec.repository;

import com.sk.skala.axcalibur.spec.entity.SpecFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;


@Repository
public interface SpecFileRepository extends JpaRepository<SpecFileEntity, Integer> {

    // 프로젝트별 파일 목록 조회
    List<SpecFileEntity> findByProjectKey(Integer projectKey);

    void deleteByProjectKey(Integer projectKey);

    // 특정 경로의 파일 존재 여부 확인
    boolean existsByPath(String path);

    
}