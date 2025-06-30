package com.sk.skala.axcalibur.feature.testcase.repository;

import java.util.List;

import com.sk.skala.axcalibur.feature.testcase.entity.TestCaseDataEntity;

public interface TestCaseDataRepositoryCustom {
    List<TestCaseDataEntity> findAllWithCategoryAndContextByTestcaseId(Integer testcaseId);
    List<TestCaseDataEntity> findAllWithParameterByTestcaseId(Integer testcaseId);
}
