package com.sk.skala.axcalibur.feature.scenario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.global.entity.ScenarioEntity;

import java.util.List;

@Repository
public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Integer> {  

    // 프로젝트 키로 시나리오 목록 조회 (생성일 기준 내림차순)
    @Query("SELECT s FROM ScenarioEntity s WHERE s.projectKey.key = :projectKey ORDER BY s.createAt DESC")
    List<ScenarioEntity> findByProjectKeyOrderByCreateAtDesc(@Param("projectKey") Integer projectKey);
    
    // 프로젝트 키로 시나리오 개수 조회
    @Query("SELECT COUNT(s) FROM ScenarioEntity s WHERE s.projectKey.key = :projectKey")
    int countByProjectKey(@Param("projectKey") Integer projectKey);
    
    // 프로젝트 키로 시나리오 존재 여부 확인
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM ScenarioEntity s WHERE s.projectKey.key = :projectKey")
    boolean existsByProjectKey(@Param("projectKey") Integer projectKey);
    
    // 프로젝트 키로 시나리오 삭제
    @Query("DELETE FROM ScenarioEntity s WHERE s.projectKey.key = :projectKey")
    void deleteByProjectKey(@Param("projectKey") Integer projectKey);
    
    // 시나리오 ID와 프로젝트 키로 특정 시나리오 조회
    @Query("SELECT s FROM ScenarioEntity s WHERE s.id = :scenarioId AND s.projectKey.key = :projectKey")
    ScenarioEntity findByIdAndProjectKey(@Param("scenarioId") String scenarioId, @Param("projectKey") Integer projectKey);
    
    // 시나리오 ID로 조회 (프로젝트 무관)
    @Query("SELECT s FROM ScenarioEntity s WHERE s.id = :scenarioId")
    ScenarioEntity findByScenarioId(@Param("scenarioId") String scenarioId);
    
    // 프로젝트 키로 시나리오 목록 조회 (간단 버전)
    @Query("SELECT s FROM ScenarioEntity s WHERE s.projectKey.key = :projectKey")
    List<ScenarioEntity> findByProjectKey(@Param("projectKey") Integer projectKey);

    // 시나리오 ID 중 최대 번호 조회 (scenario-001 형식에서 001 부분의 최대값)
    @Query("SELECT MAX(s.id) FROM ScenarioEntity s")
    String findMaxScenarioId();
    
}

