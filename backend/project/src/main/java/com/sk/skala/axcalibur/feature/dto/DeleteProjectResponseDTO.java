package com.sk.skala.axcalibur.feature.dto;

import lombok.Getter;
import lombok.Setter;

// 프로젝트 삭제 응답 DTO (IF-PR-0003)

@Getter
@Setter
public class DeleteProjectResponseDTO {
    private String result;
    
    public DeleteProjectResponseDTO() {
        this.result = "success";
    }

}