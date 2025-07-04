package com.sk.skala.axcalibur.spec.feature.report.repository;

import com.sk.skala.axcalibur.spec.feature.report.entity.TestcaseResultEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 테스트케이스 결과 엔티티를 위한 Spring Data JPA 리포지토리
 */
@Repository
public interface TestcaseResultRepository extends JpaRepository<TestcaseResultEntity, Integer>{
  List<TestcaseResultEntity> findAllByTestcase_Id(Integer testcaseId);
  List<TestcaseResultEntity> findAllByTestcase_IdIn(List<Integer> testcaseIds);
  
  /**
   * N+1 문제 해결을 위한 Fetch Join 쿼리
   * 테스트케이스 결과와 연관된 테스트케이스 엔티티를 한 번에 조회
   */
  @Query("SELECT tcr FROM TestcaseResultEntity tcr JOIN FETCH tcr.testcase WHERE tcr.testcase.id IN :testCaseIds")
  List<TestcaseResultEntity> findAllByTestcase_IdInWithFetch(@Param("testCaseIds") List<Integer> testCaseIds);
}