package com.sk.skala.axcalibur.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.entity.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {

    // 요구사항 명으로 조회 (Unique)
    Optional<Request> findByName(String name);

    // 프로젝트별 요구사항 목록 조회
    List<Request> findByProjectKey(Integer projectKey);

    // 우선순위별 요구사항 목록 조회
    List<Request> findByPriorityKey(Integer priorityKey);

    // 요구사항명 존재 여부 확인
    boolean existsByName(String name);
}
