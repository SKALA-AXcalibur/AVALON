package com.sk.skala.axcalibur.feature.apilist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk.skala.axcalibur.feature.apilist.dto.MappingResponseDto;
import com.sk.skala.axcalibur.feature.apilist.service.ApiMappingService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/list/v1")
public class ApiListController {

    private final ApiMappingService apiMappingService;

    public ApiListController(ApiMappingService apiMappingService) {
        this.apiMappingService = apiMappingService;
    }
    
    // 1. API 매핑 실행 (매핑 요청 데이터로 매핑 및 결과 반환) (IF-AL-0002)
    @PostMapping("/create")
    public ResponseEntity<MappingResponseDto> doApiMapping(
        @CookieValue(name = "avalon") String avalon, HttpServletRequest request
    ) throws JsonProcessingException {
        // avalon 값만으로 바로 매핑표 생성
        MappingResponseDto response = apiMappingService.doApiMapping(avalon);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }
}