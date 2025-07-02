package com.sk.skala.axcalibur.spec.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 설정 클래스
 * - 외부 API 호출을 위한 RestTemplate 빈 등록
 */
@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate 빈 등록
     * @return RestTemplate 인스턴스
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    } 