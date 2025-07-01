package com.sk.skala.axcalibur.scenario.feature.apilist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.ApiMappingRequestDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.MappingRequestDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.MappingResponseDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.service.ApiMappingService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/list/v1")
public class ApiListController {

    private final ApiMappingService apiMappingService;

    public ApiListController(ApiMappingService apiMappingService) {
        this.apiMappingService = apiMappingService;
    }

    // 1. API 매핑 요청 접수 (IF-AL-0001)
    @PostMapping("/generate")
    public ResponseEntity<ApiMappingRequestDto> generateRequest(@CookieValue(name = "avalon") String avalon) {
        ApiMappingRequestDto requestData = apiMappingService.getApiMappingList(avalon);
        
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(requestData);
    }
    
    // 2. API 매핑 실행 (매핑 요청 데이터로 매핑 및 결과 반환) (IF-AL-0002)
    @PostMapping("/create")
    public ResponseEntity<MappingResponseDto> doApiMapping(
        @CookieValue(name = "avalon") String avalon, HttpServletRequest request
    ) throws JsonProcessingException {
        // DB에서 시나리오/Api 목록을 조회해서 매핑 요청 DTO 생성
        MappingRequestDto requestDto = apiMappingService.getMappingRequestDtoByAvalon(avalon);

        // 매핑 처리
        MappingResponseDto response = apiMappingService.doApiMapping(requestDto, avalon);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }
}