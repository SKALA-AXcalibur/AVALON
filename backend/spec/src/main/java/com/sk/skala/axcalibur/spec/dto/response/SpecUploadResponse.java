package com.sk.skala.axcalibur.spec.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 명세서 업로드 응답 DTO
 * - 업로드 처리 된 파일 정보 목록
 * - 파일 명으로 구성
 * - 추후 파일 업로드 상태(success/fail), 에러 메시지 등 확장 가능
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpecUploadResponse {
    private List<String> uploadResults;
}
