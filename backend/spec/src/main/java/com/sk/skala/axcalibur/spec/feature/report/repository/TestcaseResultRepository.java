package com.sk.skala.axcalibur.spec.feature.report.repository;

import com.sk.skala.axcalibur.spec.feature.report.entity.TestcaseResultEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestcaseResultRepository extends JpaRepository<TestcaseResultEntity, Integer>{
  List<TestcaseResultEntity> findAllByTestcase_Id(Integer testcaseId);
  List<TestcaseResultEntity> findAllByTestcase_IdIn(List<Integer> testcaseIds); 
}