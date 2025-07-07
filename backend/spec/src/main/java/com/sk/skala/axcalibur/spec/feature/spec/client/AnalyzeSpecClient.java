package com.sk.skala.axcalibur.spec.feature.spec.client;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    private RestTemplate restTemplate;
    
    // FastAPI 호출 주소 (application.yml에 정의된 값 주입)
    @Value("${project.api.analyze-url}")
    private String analyzeUrl;

    @Value("${file.basepath}")
    private String basePath;

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

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("project_id", projectId);
        form.add("requirement_file", validateFileExists(reqPath));
        form.add("interface_def", validateFileExists(defPath));
        form.add("interface_design", validateFileExists(designPath));
        form.add("database_design", validateFileExists(dbPath));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(form, headers);

        try{
            restTemplate.postForEntity(analyzeUrl, request, String.class);
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
     * 파일 존재 여부 확인 후 FileSystemResource 반환
     */
    private FileSystemResource validateFileExists(String path){
        String fullPath = basePath + (path.startsWith("/") ? path.substring(1) : path);  // 혹시 path에 /로 시작하면 중복 방지
        File file = new File(fullPath);
        if(!file.exists()) {
            throw new BusinessExceptionHandler(String.format("파일을 찾을 수 없습니다. %s", path), ErrorCode.NOT_FOUND_ERROR);       
        }
        return new FileSystemResource(file);
    }

}