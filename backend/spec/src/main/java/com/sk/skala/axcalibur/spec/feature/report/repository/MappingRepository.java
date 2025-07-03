package com.sk.skala.axcalibur.spec.feature.report.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.spec.feature.report.entity.MappingEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface MappingRepository extends JpaRepository<MappingEntity, Integer> {
    List<MappingEntity> findByScenarioKey_ScenarioId(String scenarioId);
}
