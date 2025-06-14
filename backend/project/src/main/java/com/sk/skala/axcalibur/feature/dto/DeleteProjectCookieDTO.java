package com.sk.skala.axcalibur.feature.dto;

import java.time.LocalDateTime;

import lombok.Getter;

// 프로젝트 쿠키 삭제 응답 DTO (IF-PR-0005)

@Getter
public class DeleteProjectCookieDto {
    private String requestTime;
    private String avalon;

    public DeleteProjectCookieDto() {
        this.requestTime = LocalDateTime.now().toString();
        this.avalon = "";
    }
    
    public DeleteProjectCookieDto(String requestTime, String avalon) {
        this.requestTime = requestTime;
        this.avalon = avalon;
    }
}