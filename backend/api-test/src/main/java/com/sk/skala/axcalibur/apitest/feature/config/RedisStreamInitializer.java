package com.sk.skala.axcalibur.apitest.feature.config;

import com.sk.skala.axcalibur.apitest.feature.code.StreamConstants;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisStreamInitializer {
  private final RedisTemplate<String, ApiTaskDto> redis;

  public RedisStreamInitializer(@Qualifier("apiTaskRedisTemplate") RedisTemplate<String, ApiTaskDto> redis) {
    this.redis = redis;
  }

  /**
   * 애플리케이션 시작 시 스트림과 소비자 그룹을 생성
   */
  @PostConstruct
  public void initializeStreamAndGroup() {
    try {
      // MKSTREAM 옵션을 통해 스트림이 없으면 자동 생성
      redis.opsForStream().createGroup(StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME);
    } catch (RedisSystemException e) {
      // 그룹이 이미 존재하는 경우 발생하는 예외는 정상적인 상황
      if (e.getCause() != null && e.getCause().getMessage().contains("BUSYGROUP")) {
        System.out.println("소비자 그룹 '" + StreamConstants.GROUP_NAME + "'이(가) 이미 존재합니다.");
      } else {
        System.err.println("Redis 스트림/그룹 초기화 중 오류: " + e.getMessage());
      }
    }
  }

}
