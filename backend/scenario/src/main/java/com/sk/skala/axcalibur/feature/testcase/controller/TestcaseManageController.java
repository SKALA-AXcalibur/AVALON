package com.sk.skala.axcalibur.feature.testcase.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.sk.skala.axcalibur.feature.testcase.dto.response.TestcaseListResponse;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

public interface TestcaseManageController {
    ResponseEntity<SuccessResponse<TestcaseListResponse>> getTestcaseLists(
        @PathVariable String scenarioId,
        @CookieValue("avalon") String key,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "10") int query
    );

    
}
