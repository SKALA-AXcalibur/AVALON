package com.sk.skala.axcalibur.spec.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sk.skala.axcalibur.spec.global.service.LoggingInterceptor;

/**
 * CORS를 헤재하는 설정
 * "*" 같이 전역으로 해제하는 설정은 권장되지 않으므로, 추후 도메인 제한 필요
 *
 * @author dig04214
 */

@Configuration
public class CorsConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOriginPatterns("*")
        .allowedHeaders("*")
        .exposedHeaders("*")
        .allowedMethods("*")
        .allowCredentials(true);
  }

  // 인터셉터 설정
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LoggingInterceptor());
  }
}
