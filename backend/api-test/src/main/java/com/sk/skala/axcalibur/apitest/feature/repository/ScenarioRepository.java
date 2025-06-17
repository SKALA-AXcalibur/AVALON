package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.entity.ScenarioEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Integer> {

  Optional<ScenarioEntity> findByScenarioId(String scenarioId);
  List<ScenarioEntity> findByScenarioIdIn(List<String> scenarioIds);
  List<ScenarioEntity> findByProjectKeyAndScenarioIdIn(Integer projectKey, List<String> scenarioIds);
  List<ScenarioEntity> findAllByProjectKey(Integer projectKey);
  List<ScenarioEntity> findAllByProjectKeyAndScenarioIdGreaterThanOrderByIdAsc(Integer projectKey, String scenarioId, Pageable pageable);
}

