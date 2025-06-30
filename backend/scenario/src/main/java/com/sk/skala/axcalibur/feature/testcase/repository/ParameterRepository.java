package com.sk.skala.axcalibur.feature.testcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.global.entity.ApiListEntity;
import com.sk.skala.axcalibur.global.entity.ParameterEntity;

public interface ParameterRepository extends JpaRepository<ParameterEntity, Integer> {
    List<ParameterEntity> findByApiListKey_Id(Integer apiId);
    Optional<ParameterEntity> findByApiListKey_IdAndName(Integer apiListId, String name);
    List<ParameterEntity> findByApiListKey_IdIn(List<Integer> apiIds);
    List<ParameterEntity> findAllByApiListKey(ApiListEntity apiListEntity);
    List<ParameterEntity> findByIdIn(List<Integer> ids);
}