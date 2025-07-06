package com.sk.skala.axcalibur.spec.feature.report.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.spec.feature.report.dto.BusinessFunctionResult;
import com.sk.skala.axcalibur.spec.feature.report.entity.MappingEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseEntity;

/**
 * 테스트케이스 엔티티를 위한 Spring Data JPA 리포지토리
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCaseEntity, Integer> {
    List<TestCaseEntity> findByMappingKeyIn(List<MappingEntity> mappingEntities);

    @Query("SELECT new com.sk.skala.axcalibur.spec.feature.report.dto.BusinessFunctionResult(maj.name, COUNT(tc)) " +
            "FROM TestCaseEntity tc " +
            "JOIN tc.mappingKey m " +
            "JOIN m.apiListKey a " +
            "JOIN a.requestKey r " +
            "JOIN r.majorKey maj " +
            "WHERE tc.id IN :testCaseIds " +
            "GROUP BY maj.name " +
            "ORDER BY COUNT(tc) DESC")
    List<BusinessFunctionResult> findMostUsedBusinessFunction(@Param("testCaseIds") List<Integer> testCaseIds);

    @Query("SELECT new com.sk.skala.axcalibur.spec.feature.report.dto.BusinessFunctionResult(maj.name, COUNT(tc)) " +
            "FROM TestCaseEntity tc " +
            "JOIN tc.mappingKey m " +
            "JOIN m.scenarioKey s " +
            "JOIN m.apiListKey a " +
            "JOIN a.requestKey r " +
            "JOIN r.majorKey maj " +
            "WHERE s.projectKey.key = :projectKey " +
            "GROUP BY maj.name " +
            "ORDER BY COUNT(tc) DESC")
    List<BusinessFunctionResult> findMostUsedBusinessFunctionByProjectKey(@Param("projectKey") Integer projectKey);
}