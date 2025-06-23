package com.sk.skala.axcalibur.feature.scenario.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;

/**
 * ScenarioGenClient    
 * FastAPI로 시나리오 생성 요청을 전송하고 응답을 받는 클라이언트 클래스
 * - 요청: camelCase → snake_case 변환
 * - 응답: snake_case → camelCase 변환
 */ 
@Component
public class ScenarioGenClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    // 요청용: camelCase → snake_case
    private final ObjectMapper requestMapper;
    // 응답용: snake_case 읽기
    private final ObjectMapper responseMapper;
    // 최종 출력용: camelCase 출력
    private final ObjectMapper camelCaseMapper;
    
    // FastAPI 호출 주소 (application-dev.yml에 정의된 값 주입)
    @Value("${project.api.generate_scenario_url}")
    private String generateScenarioUrl;
    
    public ScenarioGenClient() {
        // 요청용 ObjectMapper (camelCase → snake_case)
        this.requestMapper = new ObjectMapper();
        this.requestMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        
        // 응답 읽기용 ObjectMapper (snake_case 파싱)
        this.responseMapper = new ObjectMapper();
        this.responseMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        
        // 최종 출력용 ObjectMapper (camelCase 출력)
        this.camelCaseMapper = new ObjectMapper();
        // 기본 설정 (camelCase)
    }
    
    /**
     * FastAPI로 시나리오 생성 요청 전송하고 응답 받기
     * @param projectId 프로젝트 ID
     * @param requestBody 전송할 요청 데이터 (camelCase)
     * @return FastAPI 응답 JSON (camelCase로 변환됨)
     */
    public String sendInfoAndGetResponse(String projectId, Object requestBody) {

        try {
            //  camelCase 객체 → snake_case JSON 문자열
            String jsonBody = requestMapper.writeValueAsString(requestBody);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            // FastAPI로 요청 전송하고 응답 받기
            ResponseEntity<String> response = restTemplate.postForEntity(generateScenarioUrl, entity, String.class);
            String fastApiResponse = response.getBody();
            
            // 응답 검증
            if (fastApiResponse == null || fastApiResponse.trim().isEmpty()) {
                throw new BusinessExceptionHandler("FastAPI 응답이 비어있습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            }
            
            //: snake_case JSON → 객체 → camelCase JSON 변환
            Object responseObject = responseMapper.readValue(fastApiResponse, Object.class);
            String camelCaseResponse = camelCaseMapper.writeValueAsString(responseObject);
            
            return camelCaseResponse; // camelCase로 변환된 JSON 반환
            
        } catch (Exception e) {
            if (e instanceof HttpStatusCodeException) {
                HttpStatusCodeException httpException = (HttpStatusCodeException) e;
                throw new BusinessExceptionHandler(
                    String.format("FastAPI 호출 실패: %s, 응답: %s", httpException.getStatusCode(), httpException.getResponseBodyAsString()), 
                    ErrorCode.INTERNAL_SERVER_ERROR
                );
            } else if (e instanceof RestClientException) {
                throw new BusinessExceptionHandler(
                    "FastAPI 통신 오류: " + e.getMessage(),
                    ErrorCode.INTERNAL_SERVER_ERROR
                );
            } else {
                throw new BusinessExceptionHandler(
                    "JSON 변환 오류: " + e.getMessage(),
                    ErrorCode.INTERNAL_SERVER_ERROR
                );
            }
        }
    }
}