package com.sk.skala.axcalibur.feature.testcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.global.entity.MappingEntity;

public interface MappingRepository extends JpaRepository<MappingEntity, Integer>, MappingRepositoryCustom {
    List<MappingEntity> findByScenarioKey_Id(Integer scenarioId);
    Optional<MappingEntity> findByScenarioKey_IdAndApiListKey_Id(Integer scenarioKey, Integer apiListKey);

}