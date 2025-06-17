package com.sk.skala.axcalibur.feature.testcase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.feature.testcase.entity.MappingEntity;

public interface MappingRepository extends JpaRepository<MappingEntity, Integer> {
    List<MappingEntity> findByScenario_Id(Integer scenarioId);
}