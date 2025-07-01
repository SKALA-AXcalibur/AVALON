package com.sk.skala.axcalibur.feature.testcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.feature.testcase.entity.TestCaseDataEntity;

public interface TestCaseDataRepository extends JpaRepository<TestCaseDataEntity, Integer>, TestCaseDataRepositoryCustom {
}
