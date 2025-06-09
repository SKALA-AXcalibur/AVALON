package com.sk.skala.axcalibur.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.entity.ApiList;

@Repository
public interface ApiListRepository extends JpaRepository<ApiList, Integer> {

    // API 목록 ID로 조회 (Unique)
    Optional<ApiList> findByApiListId(String apiListId);

    // 프로젝트별 API 목록 조회
    List<ApiList> findByProjectKey(Integer projectKey);
    
    // HTTP 메서드별 API 목록 조회
    List<ApiList> findByMethod(String method);

    // API 목록 ID 존재 여부 확인
    boolean existsByApiListId(String apiListId);

    // 프로젝트 + HTTP 메서드별 조회
    List<ApiList> findByProjectKeyAndMethod(Integer projectKey, String method);
}
