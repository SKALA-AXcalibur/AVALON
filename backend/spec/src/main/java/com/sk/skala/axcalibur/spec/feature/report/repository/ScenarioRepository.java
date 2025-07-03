package com.sk.skala.axcalibur.spec.feature.report.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sk.skala.axcalibur.spec.feature.report.entity.ScenarioEntity;

@Repository
public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Integer> {
    Optional<ScenarioEntity> findByScenarioId(String scenarioId);

    List<ScenarioEntity> findByProject_Id(Integer projectKey);
}
