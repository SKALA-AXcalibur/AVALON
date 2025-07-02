package com.sk.skala.axcalibur.spec.feature.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.spec.feature.project.entity.ApiListEntity;
import com.sk.skala.axcalibur.spec.global.entity.ProjectEntity;

@Repository
public interface ApiListRepository extends JpaRepository<ApiListEntity, Integer> {

    // API 목록 ID로 조회 (Unique) - 필드명을 'id'로 수정
    Optional<ApiListEntity> findById(String id);

    // 프로젝트별 API 목록 조회
    List<ApiListEntity> findByProjectKey(ProjectEntity projectKey);
    
    // HTTP 메서드별 API 목록 조회
    List<ApiListEntity> findByMethod(String method);

    // API 목록 ID 존재 여부 확인 - 필드명을 'id'로 수정
    boolean existsById(String id);

    // 프로젝트 + HTTP 메서드별 조회
    List<ApiListEntity> findByProjectKeyAndMethod(ProjectEntity projectKey, String method);
}