package com.sk.skala.axcalibur.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.global.entity.TestCaseDataEntity;

public interface TestCaseDataRepository extends JpaRepository<TestCaseDataEntity, Integer>, TestCaseDataRepositoryCustom {
}
