package com.sk.skala.axcalibur.global.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.global.entity.ApiListEntity;

@Repository
public interface ApiListRepository extends JpaRepository<ApiListEntity, Integer> {

    // 프로젝트 키로 API 목록 조회
    List<ApiListEntity> findByProjectKeyId(Integer projectId);
}