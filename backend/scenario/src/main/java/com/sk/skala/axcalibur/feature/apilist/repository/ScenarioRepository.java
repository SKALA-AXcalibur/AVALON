package com.sk.skala.axcalibur.feature.apilist.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.global.entity.ScenarioEntity;

@Repository
public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Integer> {

    /** 
     * 프로젝트 ID로 시나리오 조회
     * @param projectId 프로젝트 ID
     * @return 시나리오 리스트
     */
    List<ScenarioEntity> findByProjectId(Integer projectId);

    /** 
     * 시나리오 ID로 시나리오 조회
     * @param id 시나리오 ID
     * @return 시나리오
     */
    Optional<ScenarioEntity> findById(Integer id);

    /** 
     * 시나리오 ID 목록으로 시나리오 조회
     * @param ids 시나리오 ID 목록
     * @return 시나리오 리스트
     */
    List<ScenarioEntity> findByIdIn(Collection<Integer> ids);
}
