package com.sk.skala.axcalibur.feature.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.entity.ApiListEntity;
import com.sk.skala.axcalibur.feature.entity.MappingEntity;
import com.sk.skala.axcalibur.feature.entity.ScenarioEntity;

@Repository
public interface MappingRepository extends JpaRepository<MappingEntity, Integer> {
    
    // 매핑 ID로 조회 (Unique)
    Optional<MappingEntity> findById(String id);

    // 시나리오별 매핑 목록 조회
    List<MappingEntity> findByScenarioKeyOrderByStep(ScenarioEntity scenarioKey);

    // API별 매핑 목록 조회
    List<MappingEntity> findByApiListKey(ApiListEntity apiListKey);

    // 특정 단계의 매핑 조회
    Optional<MappingEntity> findByScenarioKeyAndStep(ScenarioEntity scenarioKey, Integer step);

    // 매핑 ID 존재 여부 확인
    boolean existsById(String id);
}
