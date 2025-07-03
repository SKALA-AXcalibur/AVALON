package com.sk.skala.axcalibur.spec.feature.report.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.spec.feature.report.entity.MappingEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseEntity;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCaseEntity, Integer> {
    List<TestCaseEntity> findByMappingKeyIn(List<MappingEntity> mappingEntities);
}