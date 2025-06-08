package com.sk.skala.axcalibur.spec.feature.spec.service;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Project ID를 조회하는 로직 구현
 * Redis에서 인증값으로 project ID를 조회합니다.
 * 쿠키에서 project key(avalon)를 추출합니다.
 */
public interface ProjectIdResolverService {
    public String resolveProjectId(HttpServletRequest request);
}
