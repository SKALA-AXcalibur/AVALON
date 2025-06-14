package com.sk.skala.axcalibur.apitest.feature.controller;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ExcuteApiTestRequestDto;
import com.sk.skala.axcalibur.apitest.feature.service.ApiTestService;
import com.sk.skala.axcalibur.apitest.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/test/v1")
public class ApiTestControllerImpl implements ApiTestController{

  private final ApiTestService svc;


  @PostMapping
  @Override
  public ResponseEntity<SuccessResponse<?>> executeApiTest(
      ExcuteApiTestRequestDto dto,
      @CookieValue(name = "avalon") String avalon) {
    return null;
  }

  @GetMapping("/{testExecutionId}")
  @Override
  public ResponseEntity<SuccessResponse<?>> getApiTestResult(
      @PathVariable(name = "testExecutionId") String testExecutionId,
      @CookieValue(name = "avalon") String avalon) {
    return null;
  }
}
