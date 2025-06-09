package com.sk.skala.axcalibur.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.entity.Priority;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Integer> {

    // 우선순위 명으로 조회 (Unique)
    Optional<Priority> findByName(String name);

    // 우선순위명 존재 여부 확인
    boolean existsByName(String name);
}
