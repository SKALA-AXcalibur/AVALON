package com.sk.skala.axcalibur.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.entity.Parameter;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Integer> {
    
    // 파라미터 ID로 조회 (Unique)
    Optional<Parameter> findByParameterId(String parameterId);

    // API별 파라미터 목록 조회
    List<Parameter> findByApiListKey(Integer apiListKey);

    // 카테고리별 파라미터 목록 조회
    List<Parameter> findByCategoryKey(Integer categoryKey);

    // 컨텍스트별 파라미터 목록 조회
    List<Parameter> findByContextKey(Integer contextKey);
    
    // 상위 파라미터별 하위 파라미터 조회 (Self-Join)
    List<Parameter> findByParentKey(Integer parentKey);

    //필수 파라미터만 조회
    List<Parameter> findByApiListKeyAndRequiredTrue(Integer apiListKey);

    // 파라미터 ID 존재 여부 확인
    boolean existsByParameterId(String parameterId);
    
    
}
