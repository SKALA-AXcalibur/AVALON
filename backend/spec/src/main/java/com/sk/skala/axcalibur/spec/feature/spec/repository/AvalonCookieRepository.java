package com.sk.skala.axcalibur.spec.feature.spec.repository;

import org.springframework.data.repository.CrudRepository;

import com.sk.skala.axcalibur.spec.feature.spec.entity.AvalonCookieEntity;


/**
 * Redis 쿠기값 조회 repository
 * 쿠키의 key값으로 프로젝트 key 조회
 */
public interface AvalonCookieRepository extends CrudRepository<AvalonCookieEntity, String> {
    // 기본적으로 token 기반 findById 가능
}