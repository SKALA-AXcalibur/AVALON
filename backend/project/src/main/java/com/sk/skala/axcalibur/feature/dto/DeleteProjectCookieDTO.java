package com.sk.skala.axcalibur.feature.dto;

import lombok.Getter;
import lombok.Setter;

// 프로젝트 쿠키 삭제 응답 DTO (IF-PR-0005)

@Getter
@Setter
public class DeleteProjectCookieDTO {
    private String status;
    private String message;
    
    public DeleteProjectCookieDTO() {
        this.status = "success";
        this.message = "쿠키 삭제 완료";
    }
    
    public DeleteProjectCookieDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }
}