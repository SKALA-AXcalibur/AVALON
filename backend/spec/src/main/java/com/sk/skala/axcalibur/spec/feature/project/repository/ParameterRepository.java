package com.sk.skala.axcalibur.spec.feature.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.spec.feature.project.entity.ApiListEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.CategoryEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.ContextEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.ParameterEntity;

@Repository
public interface ParameterRepository extends JpaRepository<ParameterEntity, Integer> {

    // API별 파라미터 목록 조회
    List<ParameterEntity> findByApiListKey(ApiListEntity apiListKey);

    // Self-Join을 위한 JOIN FETCH 쿼리
    @Query("SELECT p FROM ParameterEntity p LEFT JOIN FETCH p.parentKey WHERE p.apiListKey = :apiListKey")
    List<ParameterEntity> findByApiListKeyWithParent(@Param("apiListKey") ApiListEntity apiListKey);

    // 카테고리별 파라미터 목록 조회
    List<ParameterEntity> findByCategoryKey(CategoryEntity categoryKey);

    // 컨텍스트별 파라미터 목록 조회
    List<ParameterEntity> findByContextKey(ContextEntity contextKey);
    
    // 상위 파라미터별 하위 파라미터 조회 (Self-Join)
    List<ParameterEntity> findByParentKey(ParameterEntity parentKey);

    //필수 파라미터만 조회
    List<ParameterEntity> findByApiListKeyAndRequiredTrue(ApiListEntity apiListKey);

    // 프로젝트 키로 파라미터 삭제
    void deleteByApiListKey(ApiListEntity apiListKey);
    
    
}
