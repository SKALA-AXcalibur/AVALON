package com.sk.skala.axcalibur.global.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.global.entity.ApiListEntity;


@Repository
public interface ApiListRepository extends JpaRepository<ApiListEntity, Integer> {

    // 프로젝트 키로 API 목록 조회
    List<ApiListEntity> findByProjectKey_Id(Integer projectId);
    List<ApiListEntity> findByIdIn(List<Integer> ids);
    Optional<ApiListEntity> findByApiListId(String apiId);
    
    // API 이름과 프로젝트 키로 API 조회 (매핑 생성용)
    Optional<ApiListEntity> findByNameAndProjectKey_Id(String name, Integer projectId);
}


