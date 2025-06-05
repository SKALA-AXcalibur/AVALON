package com.sk.skala.axcalibur.spec.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 명세서 문서 정의 DTO
 * - 파일명, 확장자, 저장 경로, 파일 원문 담기 위한 구조
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecFileMeta {
    private String fileName;    // 파일명
    private String extension;   // 확장자
    private String savedPath;   // PVC 저장 경로
    private MultipartFile file; // 파일
}
