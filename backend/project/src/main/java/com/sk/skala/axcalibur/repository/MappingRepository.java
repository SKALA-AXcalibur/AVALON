package com.sk.skala.axcalibur.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.entity.Mapping;

@Repository
public interface MappingRepository extends JpaRepository<Mapping, Integer> {
    
    // 매핑 ID로 조회 (Unique)
    Optional<Mapping> findByMappingId(String mappingId);

    // 시나리오별 매핑 목록 조회
    List<Mapping> findByScenarioKeyOrderByStep(Integer scenarioKey);

    // API별 매핑 목록 조회
    List<Mapping> findByApiListKey(Integer apiListKey);

    // 특정 단계의 매핑 조회
    Optional<Mapping> findByScenarioKeyAndStep(Integer scenarioKey, Integer step);

    // 매핑 ID 존재 여부 확인
    boolean existsByMappingId(String mappingId);
}
