package com.sk.skala.axcalibur.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.global.entity.RequestEntity;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Integer> {
    
    // 프로젝트 키로 요구사항 목록 조회
    List<RequestEntity> findByProjectKey_Id(Integer projectKey);
}

