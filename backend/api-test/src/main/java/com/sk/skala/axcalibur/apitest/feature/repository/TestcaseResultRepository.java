package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseResultEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestcaseResultRepository extends JpaRepository<TestcaseResultEntity, Integer> {
  List<TestcaseResultEntity> findAllByTestcaseId(Integer testcaseKey);

}
