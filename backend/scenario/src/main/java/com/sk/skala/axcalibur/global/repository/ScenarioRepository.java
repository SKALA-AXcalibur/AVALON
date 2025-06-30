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
    
    // 시나리오 ID 중 최대 번호 조회 (scenario-001 형식에서 001 부분의 최대값)
    @Query(value = "SELECT id FROM scenario WHERE id LIKE 'scenario-%' ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findMaxScenarioId();
    
    // 프로젝트별 시나리오 목록 조회 (페이징)
    List<ScenarioEntity> findByProject_IdOrderByCreateAtDesc(Integer projectId, Pageable pageable);
    
    // 프로젝트별 시나리오 총 개수 조회
    int countByProject_Id(Integer projectId);

} 