package com.sk.skala.axcalibur.global.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.global.entity.ScenarioEntity;

@Repository
public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Integer> {
    List<ScenarioEntity> findByProject_Id(Integer projectKey);
    
    List<ScenarioEntity> findByProject_IdOrderByCreateAtDesc(Integer projectKey);
    
    /**
     * 시나리오 ID로 시나리오 조회
     */
    Optional<ScenarioEntity> findByScenarioId(String scenarioId);
    
    /**
     * 해당 프로젝트에서 시나리오 번호 중 최대값 가져오기
     */
    @Query(
        value = "SELECT COALESCE(MAX(CAST(SUBSTRING(id, LENGTH('scenario-') + 1) AS UNSIGNED)), 0) " +
                "FROM scenario " +
                "WHERE project_key = :projectKey AND id LIKE 'scenario-%'", 
        nativeQuery = true
    )
    Integer findMaxScenarioNoByProjectKey(@Param("projectKey") Integer projectKey);
}