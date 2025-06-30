package com.sk.skala.axcalibur.global.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.sk.skala.axcalibur.global.entity.MappingEntity;


public interface MappingRepository extends JpaRepository<MappingEntity, Integer> {
    List<MappingEntity> findByScenarioKey_Id(Integer scenarioId);

    // 시나리오 키로 매핑 데이터 삭제
    @Modifying
    void deleteByScenarioKey_Id(Integer scenarioKeyId);
} 
