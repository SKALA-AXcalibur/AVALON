package com.sk.skala.axcalibur.feature.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.project.entity.PriorityEntity;
import com.sk.skala.axcalibur.feature.project.entity.ProjectEntity;
import com.sk.skala.axcalibur.feature.project.entity.RequestEntity;

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
    
    // request.id으로 중복 체크
    boolean existsById(String id);

    // request.id으로 요구사항 조회
    Optional<RequestEntity> findById(String id);

}