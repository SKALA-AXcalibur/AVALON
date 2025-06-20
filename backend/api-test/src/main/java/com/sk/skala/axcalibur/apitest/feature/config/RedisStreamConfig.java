package com.sk.skala.axcalibur.apitest.feature.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis Stream 관련 설정을 담당하는 클래스
 * ApiTaskDto 객체를 Redis에 저장하고 조회하기 위한 RedisTemplate을 구성합니다.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisStreamConfig {

  /**
   * ApiTaskDto 객체를 저장하고 조회하기 위한 RedisTemplate 빈을 생성합니다.
   *
   * @param redisConnectionFactory Redis 연결 팩토리
   * @return ApiTaskDto를 처리할 수 있는 RedisTemplate 인스턴스
   */
  @Bean
  public RedisTemplate<String, ApiTaskDto> apiTaskRedisTemplate(
      RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, ApiTaskDto> template = new RedisTemplate<>();

    // ObjectMapper 설정
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime 직렬화를 위한 모듈 추가
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 날짜를 타임스탬프가 아닌 ISO-8601 형식으로 직렬화
    // 타입 정보 제거 - 역직렬화 문제 방지

    template.setConnectionFactory(redisConnectionFactory);
    var keySerializer = new StringRedisSerializer();
    Jackson2JsonRedisSerializer<ApiTaskDto> serializer = new Jackson2JsonRedisSerializer<>(objectMapper,
        ApiTaskDto.class);

    // 직렬화 설정
    // Key: String 형식으로 직렬화
    template.setKeySerializer(keySerializer);

    // Value: ApiTask 객체를 JSON 형식으로 직렬화
    template.setValueSerializer(serializer);

    // Hash Key: String 형식으로 직렬화
    template.setHashKeySerializer(keySerializer);

    // Hash Value: ApiTask 객체를 JSON 형식으로 직렬화
    template.setHashValueSerializer(serializer);

    return template;
  }

}
