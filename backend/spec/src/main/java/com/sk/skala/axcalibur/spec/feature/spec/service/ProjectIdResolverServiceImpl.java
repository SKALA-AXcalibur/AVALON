package com.sk.skala.axcalibur.spec.feature.spec.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.spec.global.code.ErrorCode;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Project ID를 조회하는 실제 코드 구현
 * Redis에서 인증값으로 project ID를 조회합니다.
 * 쿠키에서 project key(avalon)를 추출합니다.
 */
@Service
@RequiredArgsConstructor
public class ProjectIdResolverServiceImpl implements ProjectIdResolverService {
    private final RedisTemplate<String, String> redisTemplate;
    
    @Override
    public String resolveProjectId(HttpServletRequest request) {
        String projectKey = extractProjectKeyFromCookie(request);
        return getProjectIdFromRedis(projectKey);
    }

    // 쿠키에서 프로젝트 인증값(avalon) 추출
    private String extractProjectKeyFromCookie(HttpServletRequest request) {
         // 쿠키 자체가 존재하지 않는 경우
        if (request.getCookies() == null) {
            throw new BusinessExceptionHandler(ErrorCode.NOT_VALID_HEADER_ERROR);
        }

        for (Cookie cookie : request.getCookies()) {
            // 일치하는 값이 있다면 값 추출
            if ("avalon".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        // 쿠키는 있으나 유효하지 않은 경우
        throw new BusinessExceptionHandler(ErrorCode.NOT_VALID_COOKIE_ERROR);
    }

    // Redis에서 인증값으로 projectId 조회
    private String getProjectIdFromRedis(String projectKey) {
        Object projectId = redisTemplate.opsForValue().get(projectKey);
        // 인증된 프로젝트 키가 DB상 유효하지 않은 경우
        if (projectId == null) {
            throw new BusinessExceptionHandler(ErrorCode.NOT_FOUND_ERROR);
        }

        return projectId.toString();
    }
}