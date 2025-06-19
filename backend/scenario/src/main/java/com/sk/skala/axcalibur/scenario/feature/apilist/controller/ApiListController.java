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
    public ResponseEntity<SuccessResponse<ApiMappingRequestDto>> getApiMappingList(@CookieValue(name = "avalon") String avalon) {
        return ResponseEntity
            .status(SuccessCode.SELECT_SUCCESS.getStatus())
            .body(SuccessResponse.<ApiMappingRequestDto>builder()
                .data(apiMappingService.getApiMappingList(avalon))
                .status(SuccessCode.SELECT_SUCCESS)
                .message(SuccessCode.SELECT_SUCCESS.getMessage())
                .build());
    }
    
    // 2. API 매핑 (매핑 요청 ID로 매핑 및 결과 반환)  (IF-AL-0002)
    @PostMapping("/create")
    public ResponseEntity<SuccessResponse<MappingResponseDto>> doApiMapping(@RequestBody MappingRequestDto request) {
        return ResponseEntity
            .status(SuccessCode.UPDATE_SUCCESS.getStatus())
            .body(SuccessResponse.<MappingResponseDto>builder()
                .data(apiMappingService.doApiMapping(request))
                .status(SuccessCode.UPDATE_SUCCESS)
                .message(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build());
    }
}