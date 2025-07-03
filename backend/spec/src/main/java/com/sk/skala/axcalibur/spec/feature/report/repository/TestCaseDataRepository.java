package com.sk.skala.axcalibur.spec.feature.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseDataEntity;

public interface TestCaseDataRepository extends JpaRepository<TestCaseDataEntity, Integer> {
    
}
