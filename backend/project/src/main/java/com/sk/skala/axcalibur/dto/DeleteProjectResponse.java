package com.sk.skala.axcalibur.dto;

// 프로젝트 삭제 응답 DTO (IF-PR-0003, IF-PR-0005)
// 설계서 기준: requestTime만 응답
public class DeleteProjectResponse {
    
    private String requestTime;    // 요청 생성 시간
    
    // 기본 생성자
    public DeleteProjectResponse() {}
    
    // 생성자
    public DeleteProjectResponse(String requestTime) {
        this.requestTime = requestTime;
    }
    
    // getter
    public String getRequestTime() {
        return requestTime;
    }
    
    // setter
    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }
} 