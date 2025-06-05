package com.sk.skala.axcalibur.spec.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 명세서 업로드 요청 DTO
 * - multipart/form-data로 전송되는 파일 3개를 받기 위한 구조
 * - 요구사항정의서, 인터페이스정의서, 인터페이스설계서 모두 정의되어야 됨(인터페이스 설계서 기준)
 */

@Getter
@Setter
public class SpecUploadRequest {
    
    @NotNull(message = "요구사항 정의서 업로드 안됨")
    private MultipartFile requirementFile;

    @NotNull(message = "인터페이스 정의서 업로드 안됨")
    private MultipartFile interfaceDef;

    @NotNull(message = "인터페이스 설계서 업로드 안됨")
    private MultipartFile interfaceDesign;
}