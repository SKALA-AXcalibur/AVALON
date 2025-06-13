package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseResult;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestcaseResultRepository extends JpaRepository<TestcaseResult, Integer> {
  Optional<TestcaseResult> findByTestcaseId(Integer testcaseId);

}
