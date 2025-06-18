package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestcaseRepository extends JpaRepository<TestcaseEntity, Integer> {
  // 커서 기반 페이징: 커서가 null이면 처음부터, 아니면 해당 testcaseId보다 큰 값부터 size만큼 조회
  List<TestcaseEntity> findByMapping_Scenario_ProjectKeyAndMapping_Scenario_ScenarioIdAndTestcaseIdGreaterThanOrderByIdAsc(
      Integer projectKey, String scenarioId, String testcaseId, Pageable pageable);

  // projectKey와 scenarioId가 모두 일치하도록 수정 (전체 조회)
  List<TestcaseEntity> findByMapping_Scenario_ProjectKeyAndMapping_Scenario_ScenarioId(
      Integer projectKey, String scenarioId);
}
