package com.sk.skala.axcalibur.feature.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.entity.PriorityEntity;

@Repository
public interface PriorityRepository extends JpaRepository<PriorityEntity, Integer> {

    // 우선순위 명으로 조회 (Unique)
    Optional<PriorityEntity> findByName(String name);

    // 우선순위명 존재 여부 확인
    boolean existsByName(String name);
}
