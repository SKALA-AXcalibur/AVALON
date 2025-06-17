package com.sk.skala.axcalibur.spec.feature.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.spec.feature.project.entity.CategoryEntity;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    
    // 카테고리명으로 조회
    Optional<CategoryEntity> findByName(String name);

    // 카테고리명 존재 여부 확인
    boolean existsByName(String name);
}
