package com.sk.skala.axcalibur.spec.feature.report.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.spec.feature.report.entity.MappingEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseEntity;

/**
 * 테스트케이스 엔티티를 위한 Spring Data JPA 리포지토리
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCaseEntity, Integer> {
    List<TestCaseEntity> findByMappingKeyIn(List<MappingEntity> mappingEntities);
}