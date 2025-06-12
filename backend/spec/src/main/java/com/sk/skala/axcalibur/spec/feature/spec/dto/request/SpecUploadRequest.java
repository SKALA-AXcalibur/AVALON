package com.sk.skala.axcalibur.spec.feature.spec.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 명세서 업로드 요청 DTO
 * - multipart/form-data로 전송되는 파일 3개를 받기 위한 구조
 * - 요구사항정의서, 인터페이스정의서, 인터페이스설계서 모두 정의되어야 됨(인터페이스 설계서 기준)
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpecUploadRequest {
    
    @NotNull(message = "요구사항 정의서 업로드 안됨")
    private MultipartFile requirementFile;

    @NotNull(message = "인터페이스 정의서 업로드 안됨")
    private MultipartFile interfaceDef;

    @NotNull(message = "인터페이스 설계서 업로드 안됨")
    private MultipartFile interfaceDesign;

    @NotNull(message = "테이블 설계서 업로드 안됨")
    private MultipartFile databaseDesign;
}