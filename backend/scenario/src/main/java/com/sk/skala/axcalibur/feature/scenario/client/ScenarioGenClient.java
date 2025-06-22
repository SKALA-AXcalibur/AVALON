package com.sk.skala.axcalibur.feature.scenario.client;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
/**
 * AnalyzeSpecClient
 * FastAPI로 시나리오 생성 요청을 전송하는 클라이언트 클래스
 * - 
 * - FastAPI로부터 정상 응답(2xx)을 받지 못하면 예외 처리 
 */
@Component
public class ScenarioGenClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    // FastAPI 호출 주소 (application-dev.yml에 정의된 값 주입)
    @Value("${project.api.generate_scenario_url}")
    private String generateScenarioUrl;
    /**
     * FastAPI로 시나리오 생성성 요청 전송
     *
     * @param
     * @param 
     * @param 
     * @param 
     * @param 
     */
    public void sendInfo(String projectId) {
       
        try{
            restTemplate.postForEntity(generateScenarioUrl, projectId, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
        } catch (HttpStatusCodeException e) {
            // 4xx/5xx HTTP 오류
            throw new BusinessExceptionHandler(
                String.format("FastAPI 호출 실패: %s, 응답: %s", e.getStatusCode(), e.getResponseBodyAsString()), 
                ErrorCode.INTERNAL_SERVER_ERROR
            );
        } catch (RestClientException e) {
            // RestTemplate 관련 오류
            throw new BusinessExceptionHandler(
                "FastAPI 통신 오류" + e.getMessage(),
                ErrorCode.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * 바디에 내용 포함되었는지
     */
    private FileSystemResource validateFileExists(String path){
        File file = new File(path);
        if(!file.exists()) {
            throw new BusinessExceptionHandler(String.format("파일을 찾을 수 없습니다. %s", path), ErrorCode.NOT_FOUND_ERROR);       
        }
        return new FileSystemResource(file);
    }
}