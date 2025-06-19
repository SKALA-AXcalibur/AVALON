package com.sk.skala.axcalibur.spec.feature.spec.client;


import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders; 
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;

import com.sk.skala.axcalibur.spec.global.code.ErrorCode;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;

/**
 * AnalyzeSpecClient
 * FastAPI로 명세서 분석 요청을 전송하는 클라이언트 클래스
 * - project_id 및 4개의 파일(requirement, interface, design, db)을 전송
 * - FastAPI로부터 정상 응답(2xx)을 받지 못하면 예외 처리 
 */
@Component
public class AnalyzeSpecClient {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    // FastAPI 호출 주소 (application.yml에 정의된 값 주입)
    @Value("${project.api.analyze-url}")
    private String analyzeUrl;

    /**
     * FastAPI로 명세서 분석 요청 전송
     *
     * @param projectId 프로젝트ID
     * @param reqPath 요구사항 파일 경로
     * @param defPath 인터페이스 정의서 경로
     * @param designPath 인터페이스 설계서 경로
     * @param dbPath DB 설계서 경로
     */
    public void sendFiles(String projectId, String reqPath, String defPath, String designPath, String dbPath) {

        // 파일 존재 여부 확인
        validateFileExists(reqPath);
        validateFileExists(defPath);
        validateFileExists(designPath);
        validateFileExists(dbPath);

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("project_id", projectId);
        form.add("requirement_file", new FileSystemResource(reqPath));
        form.add("interface_def", new FileSystemResource(defPath));
        form.add("interface_design", new FileSystemResource(designPath));
        form.add("database_design", new FileSystemResource(dbPath));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(form, headers);

        try{
            ResponseEntity<String> response = restTemplate.postForEntity(
                analyzeUrl, // 추후 yml 파일에서 가져오도록 수정
                request,
                String.class);
            // 응답이 2xx가 아닌 경우
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BusinessExceptionHandler("FastAPI 호출에 실패했습니다.+"+response.getStatusCode(), ErrorCode.INTERNAL_SERVER_ERROR);
            }
        } catch (RestClientException e) {
            // RestTemplate 내부 오류
            throw new BusinessExceptionHandler("FastAPI 통신 오류: " + e.getMessage(),ErrorCode.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    public void setAnalyzeUrl(String analyzeUrl) {
        this.analyzeUrl = analyzeUrl;
    }

    /**
     * 파일 존재 여부 확인
     */
    private void validateFileExists(String Path){
        File file = new File(Path);
        if(!file.exists() == true) {
            throw new BusinessExceptionHandler("파일을 찾을 수 없습니다."+Path, ErrorCode.NOT_FOUND_ERROR);
        }
    }

}