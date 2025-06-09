package com.sk.skala.axcalibur.dto;

// 프로젝트 목록 저장 응답 DTO (IF-PR-0001)
public class SaveProjectResponse {
    
    private String requestTime;    // 요청 생성 시간
    private Object res;            // 응답 데이터
    
    // 생성자
    public SaveProjectResponse(String requestTime, Object res) {
        this.requestTime = requestTime;
        this.res = res;
    }
    
    // getter만 (응답용)
    public String getRequestTime() {
        return requestTime;
    }
    
    public Object getRes() {
        return res;
    }
} 