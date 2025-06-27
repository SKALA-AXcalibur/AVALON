package com.sk.skala.axcalibur.feature.testcase.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.testcase.entity.TestCaseEntity;
import com.sk.skala.axcalibur.feature.testcase.repository.TestCaseRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestcaseCommandServiceImpl implements TestcaseCommandService {
    private final TestCaseRepository testCaseRepository;
    
    @Override
    @Transactional
    public void deleteTestcase(String testcaseId, Integer projectId) {
        TestCaseEntity tc = testCaseRepository.findByTestcaseId(testcaseId)
            .orElseThrow(() -> new BusinessExceptionHandler("TC ID가 존제하지 않습니다.", ErrorCode.NOT_FOUND_ERROR));

        // 프로젝트 인증 확인
        if (!tc.getMappingKey().getScenarioKey().getProject().getId().equals(projectId)) {
            throw new BusinessExceptionHandler(ErrorCode.UNAUTHORIZED_COOKIE_ERROR);
        }

        // 연관 테스트데이터 삭제
        testCaseRepository.delete(tc);
    }

}
