package com.sk.skala.axcalibur.feature.testcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.feature.testcase.entity.ParameterEntity;

public interface ParameterRepository extends JpaRepository<ParameterEntity, Integer> {
    List<ParameterEntity> findByApiListKey_Key(Integer apiId);
    Optional<ParameterEntity> findByApiListKey_KeyAndName(Integer apiListKey, String name);
}