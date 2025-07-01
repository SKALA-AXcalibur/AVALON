package com.sk.skala.axcalibur.feature.testcase.service;

import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.feature.testcase.repository.AvalonCookieRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.AvalonCookieEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Project ID를 조회하는 실제 코드 구현
 * Redis에서 쿠키의 key를 기반으로 실제 project key를 추출합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectIdResolverServiceImpl implements ProjectIdResolverService {
    private final AvalonCookieRepository avalonCookieRepository;

    public Integer resolveProjectId(String key) {
        AvalonCookieEntity cookie = avalonCookieRepository.findById(key)
            .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.UNAUTHORIZED_COOKIE_ERROR));

        return cookie.getProjectKey();
    }
}
