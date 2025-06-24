package com.sk.skala.axcalibur.apitest.feature.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.apitest.feature.entity.ApiTestRedisEntity;

@Repository
public interface ApiTestRepository extends CrudRepository<ApiTestRedisEntity, Integer> {

}
