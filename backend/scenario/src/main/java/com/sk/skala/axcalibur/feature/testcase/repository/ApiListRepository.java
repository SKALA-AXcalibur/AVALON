package com.sk.skala.axcalibur.feature.testcase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.feature.testcase.entity.ApiListEntity;

public interface ApiListRepository extends JpaRepository<ApiListEntity, Integer> {
    List<ApiListEntity> findByIdIn(List<Integer> ids);
}