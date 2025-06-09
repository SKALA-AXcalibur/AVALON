package com.sk.skala.axcalibur.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.entity.RequestMinor;

@Repository
public interface RequestMinorRepository extends JpaRepository<RequestMinor, Integer> {

    // 요구사항 소분류 명으로 조회 (Unique)
    Optional<RequestMinor> findByName(String name);

    // 요구사항 소분류명 존재 여부 확인
    boolean existsByName(String name);
}
