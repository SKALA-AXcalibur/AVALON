package com.sk.skala.axcalibur.feature.scenario.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioResponseDto;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioGenRequestDto;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import reactor.core.publisher.Mono;

/**
 * ScenarioGenClient    
 * FastAPI로 시나리오 생성 요청을 전송하고 응답을 받는 클라이언트 클래스
 * - 요청: camelCase → snake_case 변환
 * - 응답: snake_case → camelCase 변환
 */ 
@Slf4j
@Component
public class ScenarioGenClient {
    
    @Autowired
    private final WebClient webClient; 
    
    private final ObjectMapper requestMapper; // 요청용: camelCase → snake_case
    private final ObjectMapper responseMapper;

    // FastAPI 호출 주소 (application-dev.yml에 정의된 값 주입)
    @Value("${project.api.generate_scenario_url}")
    private String generateScenarioUrl;
    
    public ScenarioGenClient(WebClient webClient) {
        this.webClient = webClient;
        
        // 요청용 ObjectMapper (camelCase → snake_case)
        this.requestMapper = new ObjectMapper();
        this.requestMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        
        // 응답 읽기용 ObjectMapper (snake_case 파싱)
        this.responseMapper = new ObjectMapper();
        this.responseMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }
    
    /**
     * FastAPI로 시나리오 생성 요청 전송하고 응답 받기
     * @param requestBody 전송할 요청 데이터 (camelCase)
     * @return FastAPI 응답을 ScenarioResponseDto로 반환 (비동기)
     */
    public Mono<ScenarioResponseDto> sendInfoAndGetResponse(ScenarioGenRequestDto requestBody) {
        return Mono.fromCallable(() -> {
                // camelCase 객체 → snake_case JSON 문자열
                return requestMapper.writeValueAsString(requestBody);
            })
            .flatMap(jsonBody -> 
                // WebClient로 요청 전송하고 응답 받기
                webClient.post()
                    .uri(generateScenarioUrl)
                    .contentType(MediaType.APPLICATION_JSON) // 요청 헤더 설정
                    .bodyValue(jsonBody) // 요청 바디 설정
                    .retrieve() // 응답 받기
                    .bodyToMono(String.class) // 응답 바디 타입 설정
            )
            .map(fastApiResponse -> {
                // 응답 검증
                if (fastApiResponse == null || fastApiResponse.trim().isEmpty()) {
                    throw new BusinessExceptionHandler("FastAPI 응답이 비어있습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
                }
                
                try {
                    // snake_case JSON → 객체 → camelCase JSON 변환
                    return responseMapper.readValue(fastApiResponse, ScenarioResponseDto.class);
                } catch (IOException e) {
                    throw new BusinessExceptionHandler(
                        "FastAPI 응답 처리 중 오류: " + e.getMessage(),
                        ErrorCode.INTERNAL_SERVER_ERROR
                    );
                }
            })
            .onErrorMap(WebClientResponseException.class, e -> 
                // 4xx/5xx HTTP 오류
                new BusinessExceptionHandler(
                    String.format("FastAPI 호출 실패: %s, 응답: %s", e.getStatusCode(), e.getResponseBodyAsString()), 
                    ErrorCode.INTERNAL_SERVER_ERROR
                )
            );
    }
}