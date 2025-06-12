package com.sk.skala.axcalibur.spec.feature.spec.service;

import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.spec.feature.spec.dto.ProjectContext;
import com.sk.skala.axcalibur.spec.feature.spec.entity.AvalonCookieEntity;
import com.sk.skala.axcalibur.spec.feature.spec.entity.ProjectEntity;
import com.sk.skala.axcalibur.spec.feature.spec.repository.AvalonCookieRepository;
import com.sk.skala.axcalibur.spec.feature.spec.repository.ProjectRepository;
import com.sk.skala.axcalibur.spec.global.code.ErrorCode;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Project ID를 조회하는 실제 코드 구현
 * Redis에서 쿠키의 key를 기반으로 실제 project key와 id를 추출합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectIdResolverServiceImpl implements ProjectIdResolverService {
    private final AvalonCookieRepository avalonCookieRepository;
    private final ProjectRepository projectRepository;
    
    @Override
    public ProjectContext resolveProjectId(String key) {
        // Redis의 Hash 구조에서 project_key 필드 조회
        AvalonCookieEntity cookie = avalonCookieRepository.findById(key)
            .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NOT_VALID_COOKIE_ERROR));

        Integer projectKey = cookie.getProjectKey();

        ProjectEntity project = projectRepository.findById(projectKey)
            .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.PROJECT_NOT_FOUND));

        return new ProjectContext(project);
    }
}