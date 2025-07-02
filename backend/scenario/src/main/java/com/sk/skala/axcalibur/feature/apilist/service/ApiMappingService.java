package com.sk.skala.axcalibur.feature.apilist.service;

import com.sk.skala.axcalibur.feature.apilist.dto.MappingRequestDto;
import com.sk.skala.axcalibur.feature.apilist.dto.MappingResponseDto;

public interface ApiMappingService {
    /**
     * API 매핑 (avalon 값으로 매핑 및 결과 반환)
     * @param avalon 프로젝트 ID
     * @return MappingResponseDto 매핑 결과 데이터
     * [쿠키 안내]
     * - 본 API는 인증/식별을 위해 'avalon' 쿠키(프로젝트 토큰)가 반드시 필요합니다.
     * - 'avalon' 쿠키는 HTTP 요청 헤더로 전달되며, 명세서 파라미터 표에는 포함되지 않습니다.
     * - 내부적으로 프로젝트 식별 및 데이터 조회에 사용됩니다.
     */
    MappingResponseDto doApiMapping(String avalon) throws com.fasterxml.jackson.core.JsonProcessingException;

    MappingRequestDto getMappingRequestDtoByAvalon(String avalon);
}