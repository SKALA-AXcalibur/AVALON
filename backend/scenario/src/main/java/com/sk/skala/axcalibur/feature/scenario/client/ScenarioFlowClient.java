package com.sk.skala.axcalibur.feature.scenario.client;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioFlowRequestDto;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * ScenarioFlowClient    
 * FastAPI로 시나리오 흐름도 생성 요청을 전송하고 응답을 받는 클라이언트 클래스
 * - 요청: camelCase → snake_case 변환
 * - 응답: snake_case → camelCase 변환
 */
@Slf4j
@Component
public class ScenarioFlowClient {
    
    @Autowired
    private WebClient webClient; 
    
    private final ObjectMapper requestMapper; // 요청용: camelCase → snake_case
    private final ObjectMapper responseMapper;

    // FastAPI 호출 주소 (application-dev.yml에 정의된 값 주입)
    @Value("${project.api.generate_flow_url}")
    private String generateFlowUrl;
    
    public ScenarioFlowClient() {
        // 요청용 ObjectMapper (camelCase → snake_case)
        this.requestMapper = new ObjectMapper();
        this.requestMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        
        // 응답 읽기용 ObjectMapper (snake_case 파싱)
        this.responseMapper = new ObjectMapper();
        this.responseMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }
    
    /**
     * FastAPI로 시나리오 흐름도 생성 요청 전송하고 응답 받기
     * @param requestBody 전송할 요청 데이터 (camelCase)
     * @return FastAPI 응답을 String으로 반환 (흐름도 데이터)
     */
    public String sendInfoAndGetResponse(ScenarioFlowRequestDto requestBody) {
        try {
            
            // 요청 데이터를 snake_case로 변환
            String requestJson = requestMapper.writeValueAsString(requestBody);
            
            // FastAPI 호출
            String response = webClient.post()
                .uri(generateFlowUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            return response;
            
        } catch (WebClientResponseException e) {
            log.error("FastAPI 호출 실패 - HTTP 상태: {}, 응답: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new BusinessExceptionHandler("FastAPI 호출 실패: " + e.getResponseBodyAsString(), ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            log.error("JSON 변환 실패", e);
            throw new BusinessExceptionHandler("JSON 변환 실패: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
} 