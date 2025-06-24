package com.sk.skala.axcalibur.feature.scenario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.global.entity.ApiListEntity;

import java.util.List;

@Repository
public interface ApiListRepository extends JpaRepository<ApiListEntity, Integer> {
    
    // 프로젝트 키로 API 목록 조회
    @Query("SELECT a FROM ApiListEntity a WHERE a.projectKey.key = :projectKey")
    List<ApiListEntity> findByProjectKey(@Param("projectKey") Integer projectKey);
    
    // 프로젝트 키와 HTTP 메서드로 API 조회
    @Query("SELECT a FROM ApiListEntity a WHERE a.projectKey.key = :projectKey AND a.method = :method")
    List<ApiListEntity> findByProjectKeyAndMethod(@Param("projectKey") Integer projectKey, @Param("method") String method);
    
    // API ID로 조회
    List<ApiListEntity> findById(String id);
}
