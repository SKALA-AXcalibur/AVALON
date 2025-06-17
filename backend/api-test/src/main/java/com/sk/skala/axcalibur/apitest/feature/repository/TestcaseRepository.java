package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestcaseRepository extends JpaRepository<TestcaseEntity, Integer> {
  // TODO: 커서와 사이즈 추가
  List<TestcaseEntity> findByMapping_Scenario_ScenarioId(String scenarioId);

}
