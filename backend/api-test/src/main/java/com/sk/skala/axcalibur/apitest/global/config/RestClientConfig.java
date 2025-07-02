package com.sk.skala.axcalibur.apitest.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${org.springframework.web.client.rest-client.connect-timeout:10}")
    private Integer connectTimeout;
    @Value("${org.springframework.web.client.rest-client.read-timeout:90}")
    private Integer readTimeout;

    @Bean
    @Primary
    public RestClient restClient() {
        return RestClient.builder()
                .requestFactory(clientHttpRequestFactory())
                .build();
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpClient client = HttpClient.newBuilder()
                // 연결 타임아웃: 서버에 연결을 시도하는 최대 시간
                .connectTimeout(Duration.ofSeconds(connectTimeout))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(client);

        // 읽기 타임아웃: 데이터를 읽는 최대 시간
        factory.setReadTimeout(Duration.ofSeconds(readTimeout));

        return factory;
    }

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> restClientBuilder
                // 공통 헤더 설정
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json");

        // // 공통 에러 핸들러 설정
        // .defaultStatusHandler(
        // status -> status.is4xxClientError() || status.is5xxServerError(),
        // (request, response) -> {
        // // 로깅 또는 커스텀 예외 처리
        // throw new RuntimeException("HTTP Error: " + response.getStatusCode());
        // });
    }

}
