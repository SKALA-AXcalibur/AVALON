package com.sk.skala.axcalibur.global.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.global.entity.ScenarioEntity;

@Repository
public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Integer> {
    List<ScenarioEntity> findByProject_Id(Integer projectId);
    
    // 시나리오 ID 중 최대 번호 조회 (scenario-001 형식에서 001 부분의 최대값)
    String findMaxScenarioId();
}

