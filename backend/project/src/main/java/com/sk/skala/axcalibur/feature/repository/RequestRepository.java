package com.sk.skala.axcalibur.feature.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
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

    // 프로젝트별 중복 체크
    boolean existsByProjectKeyAndName(ProjectEntity projectKey, String name);

    // 프로젝트별 요구사항 조회
    Optional<RequestEntity> findByProjectKeyAndName(ProjectEntity projectKey, String name);
}
