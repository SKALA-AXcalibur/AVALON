package com.sk.skala.axcalibur.feature.testcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.feature.testcase.entity.TestCaseEntity;

public interface TestCaseRepository extends JpaRepository<TestCaseEntity, Integer> {
    @Query("""
    SELECT tc.testcaseId
    FROM TestCaseEntity tc
    JOIN tc.mappingKey m
    JOIN m.scenarioKey s
    WHERE s.scenarioId = :scenarioId
    """)
    List<String> findAllByScenarioId(String scenarioId);
    Optional<TestCaseEntity> findByTestcaseId(String testcaseId);

    @Query("""
    SELECT tc
    FROM TestCaseEntity tc
    JOIN FETCH tc.mappingKey m
    JOIN FETCH m.scenarioKey s
    JOIN FETCH s.project
    WHERE tc.testcaseId = :testcaseId
    """)
    Optional<TestCaseEntity> findWithProjectByTestcaseId(@Param("testcaseId") String testcaseId);
}
