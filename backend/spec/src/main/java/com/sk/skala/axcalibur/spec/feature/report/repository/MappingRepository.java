package com.sk.skala.axcalibur.spec.feature.report.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.spec.feature.report.entity.MappingEntity;
import org.springframework.stereotype.Repository;

/**
 * 매핑표 엔티티를 위한 Spring Data JPA 리포지토리
 */
@Repository
public interface MappingRepository extends JpaRepository<MappingEntity, Integer> {
    List<MappingEntity> findByScenarioKey_ScenarioId(String scenarioId);
    List<MappingEntity> findByScenarioKey_ScenarioIdIn(List<String> scenarioIds);
}