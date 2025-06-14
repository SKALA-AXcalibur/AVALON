package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.entity.ScenarioEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Integer> {

  Optional<ScenarioEntity> findBySenarioId(String scenarioId);
  List<ScenarioEntity> findAllBySenarioId(List<String> scenarioIds);
}
