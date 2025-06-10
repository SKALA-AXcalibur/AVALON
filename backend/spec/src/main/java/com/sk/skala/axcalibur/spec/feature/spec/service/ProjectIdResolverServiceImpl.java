package com.sk.skala.axcalibur.spec.feature.spec.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.spec.global.code.ErrorCode;

import lombok.RequiredArgsConstructor;

/**
 * Project ID를 조회하는 실제 코드 구현
 * Redis에서 쿠키의 key를 기반으로 실제 project id를 추출합니다.
 */
@Service
@RequiredArgsConstructor
public class ProjectIdResolverServiceImpl implements ProjectIdResolverService {
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public String resolveProjectId(String key) {
        // 전달받은 uuid string을 redis 내에서 조회하고, 있다면 project ID 반환
        Object projectId = redisTemplate.opsForValue().get(key);
        if (projectId == null) {
            throw new BusinessExceptionHandler(ErrorCode.NOT_VALID_COOKIE_ERROR);
        }
        return projectId.toString();
    }
}