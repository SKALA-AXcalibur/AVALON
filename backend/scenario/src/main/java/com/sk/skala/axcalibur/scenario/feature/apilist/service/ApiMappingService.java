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
     * @return MappingResponseDto 매핑 결과 데이터
     */
    MappingResponseDto doApiMapping(MappingRequestDto request);
}