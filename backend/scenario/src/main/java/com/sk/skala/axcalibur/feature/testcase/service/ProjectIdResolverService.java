package com.sk.skala.axcalibur.feature.testcase.service;

/**
 * Project ID를 조회하는 로직 구현
 * Redis에서 쿠키의 key를 기반으로 실제 project key를 추출합니다.
 */
public interface ProjectIdResolverService {
    Integer resolveProjectId(String key);
}
