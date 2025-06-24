package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.dto.request.TestcaseResultUpdateDto;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseResultEntity;
import java.util.List;

public interface TestcaseResultRepositoryCustom {
  List<TestcaseResultEntity> findLastResultByTestcaseIn(List<TestcaseEntity> entities);

  TestcaseResultEntity update(TestcaseResultEntity entity, TestcaseResultUpdateDto dto);
}
