package com.sk.skala.axcalibur.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.entity.RequestMajor;

@Repository
public interface RequestMajorRepository extends JpaRepository<RequestMajor, Integer> {

    // 대분류 명으로 조회 (Unique)
    Optional<RequestMajor> findByName(String name);

    // 대분류명 존재 여부 확인
    boolean existsByName(String name);
}
