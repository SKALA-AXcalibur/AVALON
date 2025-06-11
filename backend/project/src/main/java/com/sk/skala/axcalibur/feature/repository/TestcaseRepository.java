package com.sk.skala.axcalibur.feature.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.entity.Mapping;
import com.sk.skala.axcalibur.feature.entity.Testcase;

@Repository
public interface TestcaseRepository extends JpaRepository<Testcase, Integer> {

    // 테스트케이스 ID로 조회 (Unique)
    Optional<Testcase> findById(String id);

    // 매핑별 테스트케이스 목록 조회
    List<Testcase> findByMappingKey(Mapping mappingKey);

    // 테스트케이스 ID 존재 여부 확인
    boolean existsById(String id);
    
}
