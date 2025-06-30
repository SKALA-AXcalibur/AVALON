package com.sk.skala.axcalibur.feature.testcase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.feature.testcase.entity.TestCaseEntity;

public interface TestCaseRepository extends JpaRepository<TestCaseEntity, Integer>, TestCaseRepositoryCustom {
    Optional<TestCaseEntity> findByTestcaseId(String testcaseId);
    Integer countByTestcaseIdStartingWith(String prefix);
    Boolean existsByTestcaseId(String testcaseId);
}
