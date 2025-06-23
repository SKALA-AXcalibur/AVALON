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
import org.springframework.data.redis.hash.Jackson2HashMapper;
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

}
