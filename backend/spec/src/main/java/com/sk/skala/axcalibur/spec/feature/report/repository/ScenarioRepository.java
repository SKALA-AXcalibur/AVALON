package com.sk.skala.axcalibur.spec.feature.report.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sk.skala.axcalibur.spec.feature.report.entity.ScenarioEntity;

/**
 * 시나리오 엔티티를 위한 Spring Data JPA 리포지토리
 */
@Repository
public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Integer> {
    Optional<ScenarioEntity> findByScenarioId(String scenarioId);

    List<ScenarioEntity> findByProject_Key(Integer projectKey);
    
}
