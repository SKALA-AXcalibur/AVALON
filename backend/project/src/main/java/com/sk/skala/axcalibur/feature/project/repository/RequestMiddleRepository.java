package com.sk.skala.axcalibur.feature.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.project.entity.RequestMiddleEntity;

@Repository
public interface RequestMiddleRepository extends JpaRepository<RequestMiddleEntity, Integer> {

    // 요구사항 중분류 명으로 조회 (Unique)
    Optional<RequestMiddleEntity> findByName(String name);

    // 요구사항 중분류명 존재 여부 확인
    boolean existsByName(String name);
}
