package com.sk.skala.axcalibur.feature.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    // 카테고리명으로 조회
    Optional<Category> findByName(String name);

    // 카테고리명 존재 여부 확인
    boolean existsByName(String name);
}
