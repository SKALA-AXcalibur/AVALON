package com.sk.skala.axcalibur.global.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sk.skala.axcalibur.global.entity.MappingEntity;


public interface MappingRepository extends JpaRepository<MappingEntity, Integer>, MappingRepositoryCustom {
    List<MappingEntity> findByScenarioKey_Id(Integer scenarioId);
    Optional<MappingEntity> findByScenarioKey_IdAndApiListKey_Id(Integer scenarioKey, Integer apiListKey);

    // 시나리오 키로 매핑 데이터 삭제
    @Modifying(clearAutomatically = true) // 캐시 무효화
    @Query("DELETE FROM MappingEntity m WHERE m.scenarioKey.id = :scenarioId")
    void deleteByScenarioKey_Id(Integer scenarioId);
    
    /**
     * 해당 프로젝트에서 매핑 번호 중 최대값 가져오기 (시나리오를 통해 프로젝트 접근)
     */
    @Query(
        value = "SELECT COALESCE(MAX(CAST(SUBSTRING(m.id, LENGTH('mapping-') + 1) AS UNSIGNED)), 0) " +
                "FROM mapping m " +
                "JOIN scenario s ON m.scenario_key = s.key " +
                "WHERE s.project_key = :projectKey " +
                "AND m.id LIKE 'mapping-%'", 
        nativeQuery = true
    )
    Integer findMaxMappingNoByProjectKey(@Param("projectKey") Integer projectKey);
} 
