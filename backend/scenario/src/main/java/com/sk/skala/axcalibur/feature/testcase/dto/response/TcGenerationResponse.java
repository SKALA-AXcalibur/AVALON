package com.sk.skala.axcalibur.feature.testcase.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TC 생성 response DTO
 * 생성된 TC들을 전달받는 request 객체 정의
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TcGenerationResponse {
    private String processedAt;             // 처리 완료 시간
    private Double validationRate;          // 검증률

    private List<TcGeneratedDataDto> tcList;     // 테스트 데이터 목록 (parameter 구조 + 예상값)
}