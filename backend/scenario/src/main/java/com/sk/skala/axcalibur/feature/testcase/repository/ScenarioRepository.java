package com.sk.skala.axcalibur.feature.testcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.global.entity.ScenarioEntity;

public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Integer> {
    List<ScenarioEntity> findByProject_Id(Integer projectId);
    Optional<ScenarioEntity> findByScenarioId(String scenarioId);
}
