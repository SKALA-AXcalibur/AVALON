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

/**
 * 테스트케이스 관리 커맨드 서비스 구현체
 * 테스트케이스의 수정 및 삭제 기능을 제공합니다.
 * - 테스트케이스와 관련된 프로젝트 인증을 수행한 뒤, 해당 리소스를 안전하게 수정 또는 삭제합니다.
 * - 테스트 데이터(value)의 수정을 처리합니다.
 */

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
            .orElseThrow(() -> new BusinessExceptionHandler("TC ID가 존재하지 않습니다.", ErrorCode.NOT_FOUND_ERROR));

        // 프로젝트 인증 확인
        validateOwnership(tc, projectId);

        // 연관 테스트데이터 삭제
        testCaseRepository.delete(tc);
    }

    @Override
    @Transactional
    public void updateTestcase(String tcId, Integer projectId, TcUpdateRequest request) {
        // 테스트케이스 조회
        TestCaseEntity tc = testCaseRepository.findWithProjectByTestcaseId(tcId)
            .orElseThrow(() -> new BusinessExceptionHandler("TC ID가 존재하지 않습니다.", ErrorCode.NOT_FOUND_ERROR));

        // 프로젝트 인증 확인
        validateOwnership(tc, projectId);

        // 수정 대상 필드가 아무것도 전달되지 않은 경우 방어
        validateUpdateRequest(request);
        
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

    // 프로젝트 인증 확인 함수(TC는 있지만 프로젝트 ID와 안 맞는 경우 → 인가(authorization) 실패)
    private void validateOwnership(TestCaseEntity tc, Integer projectId) {
        if (!tc.getMappingKey().getScenarioKey().getProject().getId().equals(projectId)) {
            throw new BusinessExceptionHandler("해당 프로젝트에 접근 권한이 없습니다.", ErrorCode.FORBIDDEN_ERROR);
        }
    }
    
    // 프로젝트 수정 가능 확인 함수
    private void validateUpdateRequest(TcUpdateRequest request) {
        if (request.getDescription() == null && 
            request.getExpectedResult() == null &&
            request.getPrecondition() == null &&
            (request.getTestDataList() == null || request.getTestDataList().isEmpty())) {
            throw new BusinessExceptionHandler("수정할 항목이 없습니다.", ErrorCode.BAD_REQUEST_ERROR);
        }
    }
}
