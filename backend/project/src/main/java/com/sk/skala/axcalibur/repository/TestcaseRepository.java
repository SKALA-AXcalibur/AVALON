package com.sk.skala.axcalibur.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.entity.Testcase;

@Repository
public interface TestcaseRepository extends JpaRepository<Testcase, Integer> {

    // 테스트케이스 ID로 조회 (Unique)
    Optional<Testcase> findByTestcaseId(String testcaseId);

    // 매핑별 테스트케이스 목록 조회
    List<Testcase> findByMappingKey(Integer mappingKey);

    // 테스트케이스 ID 존재 여부 확인
    boolean existsByTestcaseId(String testcaseId);
    
}
