package com.sk.skala.axcalibur.feature.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.entity.ApiList;
import com.sk.skala.axcalibur.feature.entity.Project;

@Repository
public interface ApiListRepository extends JpaRepository<ApiList, Integer> {

    // API 목록 ID로 조회 (Unique) - 필드명을 'id'로 수정
    Optional<ApiList> findById(String id);

    // 프로젝트별 API 목록 조회
    List<ApiList> findByProjectKey(Project projectKey);
    
    // HTTP 메서드별 API 목록 조회
    List<ApiList> findByMethod(String method);

    // API 목록 ID 존재 여부 확인 - 필드명을 'id'로 수정
    boolean existsById(String id);

    // 프로젝트 + HTTP 메서드별 조회
    List<ApiList> findByProjectKeyAndMethod(Project projectKey, String method);
}