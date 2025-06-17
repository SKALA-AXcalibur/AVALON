package com.sk.skala.axcalibur.feature.testcase.repository;

import org.springframework.data.repository.CrudRepository;

import com.sk.skala.axcalibur.feature.testcase.entity.AvalonCookieEntity;

public interface AvalonCookieRepository extends CrudRepository<AvalonCookieEntity, String> {
    // 기본적으로 token 기반 findById 가능
}
