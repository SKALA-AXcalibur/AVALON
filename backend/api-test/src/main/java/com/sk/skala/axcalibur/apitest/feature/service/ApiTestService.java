package com.sk.skala.axcalibur.apitest.feature.service;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ExcuteTestServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestCaseResultServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestResultServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ScenarioResponseDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.TestcaseInfoResponseDto;
import java.util.List;


public interface ApiTestService {
  List<String> excuteTestService(ExcuteTestServiceRequestDto dto);
  List<ScenarioResponseDto> getTestResultService(GetTestResultServiceRequestDto dto);
  List<TestcaseInfoResponseDto> getTestCaseResultService(GetTestCaseResultServiceRequestDto dto);
}
