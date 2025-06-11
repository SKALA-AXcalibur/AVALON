package com.sk.skala.axcalibur.feature.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.entity.ApiList;
import com.sk.skala.axcalibur.feature.entity.Category;
import com.sk.skala.axcalibur.feature.entity.Context;
import com.sk.skala.axcalibur.feature.entity.Parameter;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Integer> {
    
    // 파라미터 ID로 조회 (Unique)
    Optional<Parameter> findById(String id);

    // API별 파라미터 목록 조회
    List<Parameter> findByApiListKey(ApiList apiListKey);

    // 카테고리별 파라미터 목록 조회
    List<Parameter> findByCategoryKey(Category categoryKey);

    // 컨텍스트별 파라미터 목록 조회
    List<Parameter> findByContextKey(Context contextKey);
    
    // 상위 파라미터별 하위 파라미터 조회 (Self-Join)
    List<Parameter> findByParentKey(Parameter parentKey);

    //필수 파라미터만 조회
    List<Parameter> findByApiListKeyAndRequiredTrue(ApiList apiListKey);

    // 파라미터 ID 존재 여부 확인
    boolean existsById(String id);
    
    
}
