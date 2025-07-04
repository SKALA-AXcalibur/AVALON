package com.sk.skala.axcalibur.spec.feature.report.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseDataEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseEntity;

/**
 * 테스트케이스 데이터 엔티티를 위한 Spring Data JPA 리포지토리
 */
@Repository
public interface TestCaseDataRepository extends JpaRepository<TestCaseDataEntity, Integer> {
    List<TestCaseDataEntity> findByTestcaseKeyIn(List<TestCaseEntity> testCaseEntities);
    
    List<TestCaseDataEntity> findByTestcaseKey_IdIn(List<Integer> testCaseIds);
}
