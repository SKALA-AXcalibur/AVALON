package com.sk.skala.axcalibur.feature.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.entity.PriorityEntity;
import com.sk.skala.axcalibur.feature.entity.ProjectEntity;
import com.sk.skala.axcalibur.feature.entity.RequestEntity;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Integer> {

    // 프로젝트별 요구사항 목록 조회
    List<RequestEntity> findByProjectKey(ProjectEntity projectKey);

    // 우선순위별 요구사항 목록 조회
    List<RequestEntity> findByPriorityKey(PriorityEntity priorityKey);

    // 프로젝트별 중복 체크 (기존)
    boolean existsByProjectKeyAndName(ProjectEntity projectKey, String name);

    // 프로젝트별 요구사항 조회 (기존)
    Optional<RequestEntity> findByProjectKeyAndName(ProjectEntity projectKey, String name);
    
    // project.id와 request.name으로 중복 체크 (@Query 사용)
    @Query("SELECT COUNT(r) > 0 FROM RequestEntity r WHERE r.projectKey.id = :projectId AND r.name = :name")
    boolean existsByProjectKeyIdAndName(@Param("projectId") String projectId, @Param("name") String name);
    
    // project.id와 request.name으로 요구사항 조회 (@Query 사용)
    @Query("SELECT r FROM RequestEntity r WHERE r.projectKey.id = :projectId AND r.name = :name")
    Optional<RequestEntity> findByProjectKeyIdAndName(@Param("projectId") String projectId, @Param("name") String name);
}