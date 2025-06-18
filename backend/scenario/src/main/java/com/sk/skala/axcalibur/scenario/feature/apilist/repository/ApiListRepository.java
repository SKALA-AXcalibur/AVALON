package com.sk.skala.axcalibur.scenario.feature.apilist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.scenario.feature.apilist.entity.ApiListEntity;

@Repository
public interface ApiListRepository extends JpaRepository<ApiListEntity, Integer> {

    // API 목록 ID로 조회 (Unique) - 필드명을 'id'로 수정
    Optional<ApiListEntity> findById(String id);

    // 프로젝트별 API 목록 조회
    List<ApiListEntity> findByProjectKey(Integer projectKey);

}