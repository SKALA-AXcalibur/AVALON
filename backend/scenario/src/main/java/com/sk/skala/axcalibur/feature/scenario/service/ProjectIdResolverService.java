package com.sk.skala.axcalibur.feature.scenario.service;

import com.sk.skala.axcalibur.feature.scenario.dto.ProjectContext;

/**
 * Project ID를 조회하는 로직 구현
 * Redis에서 쿠키의 key를 기반으로 실제 project key와 id를 추출합니다.
 */
public interface ProjectIdResolverService {
    ProjectContext resolveProjectId(String key);
}
