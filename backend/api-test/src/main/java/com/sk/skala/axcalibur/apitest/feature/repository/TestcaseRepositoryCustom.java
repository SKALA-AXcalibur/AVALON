package com.sk.skala.axcalibur.apitest.feature.repository;


import com.sk.skala.axcalibur.apitest.feature.dto.response.TestcaseSuccessResponseDto;
import com.sk.skala.axcalibur.apitest.feature.entity.ScenarioEntity;
import java.util.List;

public interface TestcaseRepositoryCustom {
  List<TestcaseSuccessResponseDto> findByScenarioInWithResultSuccess(List<ScenarioEntity> entities);

}


