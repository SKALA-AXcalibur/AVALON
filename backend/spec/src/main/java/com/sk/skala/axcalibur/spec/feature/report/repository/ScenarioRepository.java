package com.sk.skala.axcalibur.spec.feature.report.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.spec.feature.report.entity.ScenarioEntity;

public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Integer> {
    Optional<ScenarioEntity> findByScenarioId(String scenarioId);
}
