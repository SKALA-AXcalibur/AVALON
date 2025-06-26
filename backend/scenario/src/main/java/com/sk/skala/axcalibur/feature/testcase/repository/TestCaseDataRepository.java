package com.sk.skala.axcalibur.feature.testcase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.sk.skala.axcalibur.feature.testcase.entity.TestCaseDataEntity;

public interface TestCaseDataRepository extends JpaRepository<TestCaseDataEntity, Integer>{
    @Query("""
    SELECT td
    FROM TestCaseDataEntity td
    JOIN FETCH td.parameterKey p
    JOIN FETCH p.categoryKey
    JOIN FETCH p.contextKey
    WHERE td.testcaseKey.id = :testcaseId
    """)
    List<TestCaseDataEntity> findAllWithCategoryAndContextByTestcaseId(@Param("testcaseId") Integer testcaseId);

}
