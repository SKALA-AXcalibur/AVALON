package com.sk.skala.axcalibur.feature.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.entity.AvalonCookieEntity;

import java.util.Optional;

// 아발론 쿠키 Redis 레포지토리
@Repository
public interface AvalonCookieRepository extends CrudRepository<AvalonCookieEntity, String> {
    
    // 토큰으로 쿠키 조회 (기본 findById와 동일하지만 명시적)
    Optional<AvalonCookieEntity> findByToken(String token);

    // 토큰으로 쿠키 삭제
    void deleteByToken(String token);
}