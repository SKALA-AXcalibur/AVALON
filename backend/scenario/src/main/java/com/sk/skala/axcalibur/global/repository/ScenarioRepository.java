package com.sk.skala.axcalibur.global.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.global.entity.ScenarioEntity;

@Repository
public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Integer> {
    List<ScenarioEntity> findByProject_Id(Integer projectId);
    
    // 시나리오 ID로 시나리오 조회
    Optional<ScenarioEntity> findByScenarioId(String scenarioId);
    
    // 해당 프로젝트에서 scenarioid(시나리오 번호) 중 최대값 가져오기
    @Query(
    value = "SELECT COALESCE(MAX(CAST(SUBSTRING(id, LENGTH('scenario-') + 1) AS UNSIGNED)), 0) " +
            "FROM scenario " +
            "WHERE project_key = :projectKey AND id LIKE 'scenario-%'", nativeQuery = true)
    Integer findMaxScenarioNoByProjectKey(Integer projectKey);

    // 프로젝트별 시나리오 목록 조회 (페이징)
    List<ScenarioEntity> findByProject_IdOrderByCreatedAtDesc(Integer projectId, Pageable pageable);
    
    // 프로젝트별 시나리오 총 개수 조회
    Integer countByProject_Id(Integer projectId);

} 
