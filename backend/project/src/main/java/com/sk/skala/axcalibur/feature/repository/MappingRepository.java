package com.sk.skala.axcalibur.feature.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.entity.ApiList;
import com.sk.skala.axcalibur.feature.entity.Mapping;
import com.sk.skala.axcalibur.feature.entity.Scenario;

@Repository
public interface MappingRepository extends JpaRepository<Mapping, Integer> {
    
    // 매핑 ID로 조회 (Unique)
    Optional<Mapping> findById(String id);

    // 시나리오별 매핑 목록 조회
    List<Mapping> findByScenarioKeyOrderByStep(Scenario scenarioKey);

    // API별 매핑 목록 조회
    List<Mapping> findByApiListKey(ApiList apiListKey);

    // 특정 단계의 매핑 조회
    Optional<Mapping> findByScenarioKeyAndStep(Scenario scenarioKey, Integer step);

    // 매핑 ID 존재 여부 확인
    boolean existsById(String id);
}
