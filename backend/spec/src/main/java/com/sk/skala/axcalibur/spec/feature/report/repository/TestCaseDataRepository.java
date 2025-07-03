package com.sk.skala.axcalibur.spec.feature.report.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseDataEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseEntity;

@Repository
public interface TestCaseDataRepository extends JpaRepository<TestCaseDataEntity, Integer> {
    List<TestCaseDataEntity> findByTestcaseKeyIn(List<TestCaseEntity> testCaseEntities);
}
