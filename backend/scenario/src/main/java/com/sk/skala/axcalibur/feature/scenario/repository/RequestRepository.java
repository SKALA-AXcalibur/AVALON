package com.sk.skala.axcalibur.feature.scenario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.scenario.entity.RequestEntity;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Integer> {
    
    // 프로젝트 키로 요구사항 목록 조회
    @Query("SELECT r FROM RequestEntity r WHERE r.projectKey.key = :projectKey")
    List<RequestEntity> findByProjectKey(@Param("projectKey") Integer projectKey);
    
    // 프로젝트 키와 우선순위로 요구사항 조회
    @Query("SELECT r FROM RequestEntity r WHERE r.projectKey.key = :projectKey AND r.priorityKey.key = :priorityKey")
    List<RequestEntity> findByProjectKeyAndPriorityKey(@Param("projectKey") Integer projectKey, @Param("priorityKey") Integer priorityKey);
}
