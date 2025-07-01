package com.sk.skala.axcalibur.feature.testcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.global.entity.ApiListEntity;

public interface ApiListRepository extends JpaRepository<ApiListEntity, Integer> {
    List<ApiListEntity> findByIdIn(List<Integer> ids);
    Optional<ApiListEntity> findByApiListId(String apiId);
}