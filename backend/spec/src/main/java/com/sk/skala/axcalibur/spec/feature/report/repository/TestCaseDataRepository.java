package com.sk.skala.axcalibur.spec.feature.report.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    /**
     * N+1 문제 해결을 위한 Fetch Join 쿼리
     * 테스트케이스 데이터와 연관된 테스트케이스 엔티티를 한 번에 조회
     */
    @Query("SELECT tcd FROM TestCaseDataEntity tcd JOIN FETCH tcd.testcaseKey WHERE tcd.testcaseKey.id IN :testCaseIds")
    List<TestCaseDataEntity> findByTestcaseKey_IdInWithFetch(@Param("testCaseIds") List<Integer> testCaseIds);
}
