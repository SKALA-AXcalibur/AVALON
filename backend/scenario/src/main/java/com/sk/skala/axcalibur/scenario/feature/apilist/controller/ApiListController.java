package com.sk.skala.axcalibur.scenario.feature.apilist.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.sk.skala.axcalibur.scenario.feature.apilist.dto.ApiMappingRequestDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.MappingRequestDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.MappingResponseDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.service.ApiMappingService;
import com.sk.skala.axcalibur.scenario.global.code.SuccessCode;
import com.sk.skala.axcalibur.scenario.global.response.SuccessResponse;

@RestController
@RequestMapping("/list/v1")
public class ApiListController {

    private final ApiMappingService apiMappingService;

    public ApiListController(ApiMappingService apiMappingService) {
        this.apiMappingService = apiMappingService;
    }

    // 1. API 매핑 요청 (IF-AL-0001)
    @PostMapping("/generate")
    public ResponseEntity<SuccessResponse<MappingResponseDto>> generateAndMap(@CookieValue(name = "avalon") String avalon) throws com.fasterxml.jackson.core.JsonProcessingException, com.fasterxml.jackson.databind.JsonMappingException {
        // 1. DB에서 데이터 조회 및 DTO 생성
        MappingRequestDto requestDto = apiMappingService.getMappingRequestDtoByAvalon(avalon);

        // 2. FastAPI 호출
        MappingResponseDto response = apiMappingService.doApiMapping(requestDto);

        // 3. 결과 반환
        return ResponseEntity
            .status(SuccessCode.UPDATE_SUCCESS.getStatus())
            .body(SuccessResponse.<MappingResponseDto>builder()
                .data(response)
                .status(SuccessCode.UPDATE_SUCCESS)
                .message(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build());
    }
    
    // 2. API 매핑 (매핑 요청 ID로 매핑 및 결과 반환)  (IF-AL-0002)
    @PostMapping("/create")
    public ResponseEntity<SuccessResponse<MappingResponseDto>> doApiMapping(@RequestBody MappingRequestDto request) throws com.fasterxml.jackson.core.JsonProcessingException, com.fasterxml.jackson.databind.JsonMappingException {
        return ResponseEntity
            .status(SuccessCode.UPDATE_SUCCESS.getStatus())
            .body(SuccessResponse.<MappingResponseDto>builder()
                .data(apiMappingService.doApiMapping(request))
                .status(SuccessCode.UPDATE_SUCCESS)
                .message(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build());
    }
}