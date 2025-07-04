package com.sk.skala.axcalibur.spec.feature.report.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.spec.feature.report.entity.MappingEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseEntity;

/**
 * 테스트케이스 엔티티를 위한 Spring Data JPA 리포지토리
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCaseEntity, Integer> {
    List<TestCaseEntity> findByMappingKeyIn(List<MappingEntity> mappingEntities);

    @Query("SELECT maj.name, COUNT(tc) FROM TestCaseEntity tc " +
           "JOIN tc.mappingKey m " +
           "JOIN m.apiListKey a " +
           "JOIN a.requestKey r " +
           "JOIN r.majorKey maj " +
           "WHERE tc IN :testCases " +
           "GROUP BY maj.name " +
           "ORDER BY COUNT(tc) DESC")
    List<Object[]> findMostUsedBusinessFunction(@Param("testCases") List<TestCaseEntity> testCases);
}