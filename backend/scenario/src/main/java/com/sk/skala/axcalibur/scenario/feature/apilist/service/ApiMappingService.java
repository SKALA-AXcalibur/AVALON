package com.sk.skala.axcalibur.scenario.feature.apilist.service;

import com.sk.skala.axcalibur.scenario.feature.apilist.dto.ApiMappingRequestDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.MappingRequestDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.MappingResponseDto;

public interface ApiMappingService {
    /**
     * 1. API 매핑 요청 (프로젝트 ID로 API/시나리오 목록 조회)
     * @param avalon 프로젝트 ID
     * @return ApiMappingRequestDto 매핑 요청 데이터
     */ 
    ApiMappingRequestDto getApiMappingList(String avalon);
    
    /**
     * 2. API 매핑 (매핑 요청 ID로 매핑 및 결과 반환)
     * @param request 매핑 요청 데이터 (시나리오 및 API 목록)
     * @param avalon 프로젝트 ID
     * @return MappingResponseDto 매핑 결과 데이터
     * [쿠키 안내]
     * - 본 API는 인증/식별을 위해 'avalon' 쿠키(프로젝트 토큰)가 반드시 필요합니다.
     * - 'avalon' 쿠키는 HTTP 요청 헤더로 전달되며, 명세서 파라미터 표에는 포함되지 않습니다.
     * - 내부적으로 프로젝트 식별 및 데이터 조회에 사용됩니다.
     */
    MappingResponseDto doApiMapping(MappingRequestDto request, String avalon) throws com.fasterxml.jackson.core.JsonProcessingException, com.fasterxml.jackson.databind.JsonMappingException, com.sk.skala.axcalibur.scenario.global.exception.BusinessExceptionHandler;

    MappingRequestDto getMappingRequestDtoByAvalon(String avalon);
}