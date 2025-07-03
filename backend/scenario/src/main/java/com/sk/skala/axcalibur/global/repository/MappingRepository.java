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
     * 시나리오 키로 매핑 데이터 삭제
     */
    @Modifying(clearAutomatically = true) // 캐시 무효화
    @Query("DELETE FROM MappingEntity m WHERE m.scenarioKey.id IN :scenarioIds")
    void deleteAllByScenarioKey(@Param("scenarioIds") List<Integer> scenarioIds);
} 
