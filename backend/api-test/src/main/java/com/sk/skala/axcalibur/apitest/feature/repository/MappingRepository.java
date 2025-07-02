package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.entity.MappingEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MappingRepository extends JpaRepository<MappingEntity, Integer>, MappingRepositoryCustom {

    List<MappingEntity> findByScenario_ProjectKeyAndScenario_ScenarioIdInOrderByStepAsc(
            Integer projectKey, List<String> scenarioIds);

    List<MappingEntity> findByScenario_ScenarioIdOrderByStepAsc(String scenarioId);
}
