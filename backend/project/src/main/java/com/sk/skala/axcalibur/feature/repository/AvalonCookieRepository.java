package com.sk.skala.axcalibur.feature.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.entity.AvalonCookieEntity;

import java.util.List;
import java.util.Optional;

// 아발론 쿠키 Redis 레포지토리
@Repository
public interface AvalonCookieRepository extends CrudRepository<AvalonCookieEntity, String> {
    
    // 프로젝트 키로 쿠키 목록 조회
    // 하나의 프로젝트에 여러 쿠키가 있을 수 있음 (여러 브라우저/탭)
    List<AvalonCookieEntity> findByProjectKey(Integer projectKey);
    
    // 토큰으로 쿠키 조회 (기본 findById와 동일하지만 명시적)
    Optional<AvalonCookieEntity> findByToken(String token);
    
    // 프로젝트의 모든 쿠키 삭제
    // 프로젝트 삭제 시 관련 쿠키들 정리용
    void deleteByProjectKey(Integer projectKey);

    // 토큰으로 쿠키 삭제
    void deleteByToken(String token);

    // 특정 프로젝트의 쿠키 개수 조회
    // 동시 접속자 수 파악용
    long countByProjectKey(Integer projectKey);
    
    // 모든 쿠키 개수 조회
    // 전체 활성 세션 수 파악용
    long count();
    
    // 특정 프로젝트에 쿠키가 존재하는지 확인
    boolean existsByProjectKey(Integer projectKey);
}