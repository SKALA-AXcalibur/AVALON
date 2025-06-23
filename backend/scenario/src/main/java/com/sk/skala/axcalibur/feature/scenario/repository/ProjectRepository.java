package com.sk.skala.axcalibur.feature.scenario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.scenario.entity.ProjectEntity;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Integer> {

    // 프로젝트 ID로 조회 (비즈니스 키)
    Optional<ProjectEntity> findById(String id);

    // 프로젝트 키로 조회 (PK)
    Optional<ProjectEntity> findByKey(Integer key);

    // 프로젝트 ID 존재 여부
    boolean existsById(String id);
}