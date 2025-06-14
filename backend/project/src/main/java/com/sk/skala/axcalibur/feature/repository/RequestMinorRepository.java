package com.sk.skala.axcalibur.feature.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.entity.RequestMinorEntity;

@Repository
public interface RequestMinorRepository extends JpaRepository<RequestMinorEntity, Integer> {

    // 요구사항 소분류 명으로 조회 (Unique)
    Optional<RequestMinorEntity> findByName(String name);

    // 요구사항 소분류명 존재 여부 확인
    boolean existsByName(String name);
}
