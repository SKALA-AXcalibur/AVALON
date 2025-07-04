package com.sk.skala.axcalibur.spec.feature.report.repository;

import com.sk.skala.axcalibur.spec.feature.report.entity.TestcaseResultEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 테스트케이스 결과 엔티티를 위한 Spring Data JPA 리포지토리
 */
@Repository
public interface TestcaseResultRepository extends JpaRepository<TestcaseResultEntity, Integer>{
  List<TestcaseResultEntity> findAllByTestcase_Id(Integer testcaseId);
  List<TestcaseResultEntity> findAllByTestcase_IdIn(List<Integer> testcaseIds); 
}