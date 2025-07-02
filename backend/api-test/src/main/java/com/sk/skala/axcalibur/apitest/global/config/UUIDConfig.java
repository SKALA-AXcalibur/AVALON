package com.sk.skala.axcalibur.apitest.global.config;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UUIDConfig {
  /**
   * UUID v7 생성기를 제공하는 Bean
   * 타임스탬프 기반의 UUIDv7을 생성하여 시간 순서가 보장되는 UUID를 생성합니다.
   * 데이터베이스 인덱싱 및 성능 최적화에 유용합니다.
   *
   * @return TimeBasedGenerator UUID v7 생성기
   */
  @Bean
  public TimeBasedGenerator uuidV7Generator() {
      return Generators.timeBasedGenerator();
  }
}
