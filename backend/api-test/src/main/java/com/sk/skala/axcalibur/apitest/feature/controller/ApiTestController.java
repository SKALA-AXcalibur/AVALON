package com.sk.skala.axcalibur.apitest.feature.controller;

import com.sk.skala.axcalibur.apitest.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/test/v1")
public class ApiTestController implements IApiTestController{

  @Override
  public ResponseEntity<SuccessResponse<?>> executeApiTest(String scenarioId, String avalon) {
    return null;
  }

  @Override
  public ResponseEntity<SuccessResponse<?>> getApiTestResult(String testExecutionId,
      String avalon) {
    return null;
  }
}
