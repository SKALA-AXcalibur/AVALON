package com.sk.skala.axcalibur.feature.scenario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.scenario.entity.DbDesignEntity;

import java.util.List;

@Repository
public interface DbDesignRepository extends JpaRepository<DbDesignEntity, Integer> {
    
    // 프로젝트 키로 DB 설계서 목록 조회 (컬럼 정보 포함)
    @Query("SELECT d FROM DbDesignEntity d LEFT JOIN FETCH d.columns WHERE d.projectKey.key = :projectKey")
    List<DbDesignEntity> findByProjectKeyWithColumns(@Param("projectKey") Integer projectKey);
    
    // 프로젝트 키로 DB 설계서 목록 조회
    @Query("SELECT d FROM DbDesignEntity d WHERE d.projectKey.key = :projectKey")
    List<DbDesignEntity> findByProjectKey(@Param("projectKey") Integer projectKey);
}
