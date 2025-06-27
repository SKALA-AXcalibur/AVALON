package com.sk.skala.axcalibur.feature.testcase.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.testcase.dto.request.TcUpdateRequest;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcParamDto;
import com.sk.skala.axcalibur.feature.testcase.entity.TestCaseDataEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.TestCaseEntity;
import com.sk.skala.axcalibur.feature.testcase.repository.TestCaseDataRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.TestCaseRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TcCommandServiceImpl implements TcCommandService {
    private final TestCaseRepository testCaseRepository;
    private final TestCaseDataRepository testCaseDataRepository;
    
    @Override
    @Transactional
    public void deleteTestcase(String tcId, Integer projectId) {
        // 테스트케이스 조회
        TestCaseEntity tc = testCaseRepository.findWithProjectByTestcaseId(tcId)
            .orElseThrow(() -> new BusinessExceptionHandler("TC ID가 존제하지 않습니다.", ErrorCode.NOT_FOUND_ERROR));

        // 프로젝트 인증 확인
        if (!tc.getMappingKey().getScenarioKey().getProject().getId().equals(projectId)) {
            throw new BusinessExceptionHandler(ErrorCode.UNAUTHORIZED_COOKIE_ERROR);
        }

        // 연관 테스트데이터 삭제
        testCaseRepository.delete(tc);
    }

    @Override
    @Transactional
    public void updateTestcase(String tcId, Integer projectId, TcUpdateRequest request) {
        // 테스트케이스 조회
        TestCaseEntity tc = testCaseRepository.findWithProjectByTestcaseId(tcId)
            .orElseThrow(() -> new BusinessExceptionHandler("TC ID가 존제하지 않습니다.", ErrorCode.NOT_FOUND_ERROR));

        // 프로젝트 인증 확인
        if (!tc.getMappingKey().getScenarioKey().getProject().getId().equals(projectId)) {
            throw new BusinessExceptionHandler(ErrorCode.UNAUTHORIZED_COOKIE_ERROR);
        }

        // TC 정보 수정
        tc.update(request.getPrecondition(), request.getDescription(), request.getExpectedResult());

        // 테스트 데이터 수정
        if (request.getTestDataList() != null) {
            Map<Integer, String> paramMap = new HashMap<>();
            for (TcParamDto dto : request.getTestDataList()) {
                Integer key = dto.getParamId();
                if (key != null) {
                    paramMap.put(key, dto.getValue());  // value의 null 허용
                }
            }
            List<TestCaseDataEntity> dataList = testCaseDataRepository
                .findAllWithParameterByTestcaseId(tc.getId());

            for (TestCaseDataEntity data : dataList) {
                Integer paramId = data.getParameterKey().getId();
                if (paramMap.containsKey(paramId)) {
                    String value = paramMap.get(paramId);
                    data.updateValue(value);
                }
            }
        }
    }
}
