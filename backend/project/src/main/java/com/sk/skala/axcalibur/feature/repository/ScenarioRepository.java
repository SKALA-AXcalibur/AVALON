package com.sk.skala.axcalibur.feature.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.entity.ProjectEntity;
import com.sk.skala.axcalibur.feature.entity.ScenarioEntity;

@Repository
public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Integer> {
    
    // 시나리오 ID로 조회 (Unique)
    Optional<ScenarioEntity> findById(String id);

    // 프로젝트별 시나리오 목록 조회
    List<ScenarioEntity> findByProjectKey(ProjectEntity projectKey);

    // 시나리오명으로 조회
    List<ScenarioEntity> findByName(String name);

    // 시나리오 ID 존재 여부 확인
    boolean existsById(String id);
    
    

}
