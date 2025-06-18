package com.sk.skala.axcalibur.spec.feature.spec.client;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders; 
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.sk.skala.axcalibur.spec.global.code.ErrorCode;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;

public class AnalyzeSpecClient {
    
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendFiles(String projectId, String reqPath, String defPath, String designPath, String dbPath) {
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("projectId", projectId);
        form.add("requirementFile", new FileSystemResource(reqPath));
        form.add("interfaceDef", new FileSystemResource(defPath));
        form.add("interfaceDesign", new FileSystemResource(designPath));
        form.add("databaseDesign", new FileSystemResource(dbPath));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(form, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:8000/api/spec/v1/analyze", // 추후 yml 파일에서 가져오도록 수정
            request,
            String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new BusinessExceptionHandler("FastAPI 호출에 실패했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }
    
}