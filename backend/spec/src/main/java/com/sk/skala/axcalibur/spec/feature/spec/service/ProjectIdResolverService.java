package com.sk.skala.axcalibur.spec.feature.spec.service;

/**
 * Project ID를 조회하는 로직 구현
 * Redis에서 쿠키의 key를 기반으로 실제 project id를 추출합니다.
 */
public interface ProjectIdResolverService {
    public String resolveProjectId(String key);
}
