package com.sk.skala.axcalibur.apitest.feature.repository;

import org.springframework.data.repository.CrudRepository;

import com.sk.skala.axcalibur.apitest.feature.entity.ApiTestDetailRedisEntity;

public interface ApiTestDetailRepository extends CrudRepository<ApiTestDetailRedisEntity, String> {

}
