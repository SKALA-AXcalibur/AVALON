package com.sk.skala.axcalibur.feature.apilist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.global.entity.ApiListEntity;
import com.sk.skala.axcalibur.global.entity.MappingEntity;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface MappingRepository extends JpaRepository<MappingEntity, Integer> {
    /** 
     * 매핑 엔티티 조회
     * @param id 매핑 엔티티 ID
     * @return 매핑 엔티티
     */
    Optional<MappingEntity> findById(Integer id);

    /** 
     * 시나리오 키로 매핑 엔티티 조회
     * @param scenarioKey 시나리오 키
     * @return 매핑 엔티티 리스트
     */
    List<MappingEntity> findByScenarioKey(ScenarioEntity scenarioKey);

    /** 
     * API 목록 키로 매핑 엔티티 조회
     * @param apiListKey API 목록 키
     * @return 매핑 엔티티 리스트
     */
    List<MappingEntity> findByApiListKey(ApiListEntity apiListKey);
}
