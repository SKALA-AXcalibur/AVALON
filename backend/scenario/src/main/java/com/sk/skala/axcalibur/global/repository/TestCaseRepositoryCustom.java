package com.sk.skala.axcalibur.global.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sk.skala.axcalibur.global.entity.TestCaseEntity;

public interface TestCaseRepositoryCustom {
    Page<String> findAllByScenarioId(String scenarioId, Pageable pageable);
    Optional<TestCaseEntity> findWithProjectByTestcaseId(String testcaseId);
}
