package com.sk.skala.axcalibur.global.repository;

import java.util.List;

import com.sk.skala.axcalibur.global.entity.TestCaseDataEntity;

public interface TestCaseDataRepositoryCustom {
    List<TestCaseDataEntity> findAllWithCategoryAndContextByTestcaseId(Integer testcaseId);
    List<TestCaseDataEntity> findAllWithParameterByTestcaseId(Integer testcaseId);
}
