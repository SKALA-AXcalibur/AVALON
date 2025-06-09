package com.sk.skala.axcalibur.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.entity.Context;

@Repository
public interface ContextRepository extends JpaRepository<Context, Integer> {
    
    // 컨텍스트명으로 조회
    Optional<Context> findByName(String name);

    // 컨텍스트명 존재 여부 확인
    boolean existsByName(String name);
}
