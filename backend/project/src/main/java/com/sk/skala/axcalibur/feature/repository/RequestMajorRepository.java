package com.sk.skala.axcalibur.feature.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.entity.RequestMajorEntity;

@Repository
public interface RequestMajorRepository extends JpaRepository<RequestMajorEntity, Integer> {

    // 대분류 명으로 조회 (Unique)
    Optional<RequestMajorEntity> findByName(String name);

    // 대분류명 존재 여부 확인
    boolean existsByName(String name);
}
