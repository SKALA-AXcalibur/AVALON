package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseResultEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestcaseResultRepository extends JpaRepository<TestcaseResultEntity, Integer> {
  Optional<TestcaseResultEntity> findByTestcaseId(Integer testcaseId);

}
