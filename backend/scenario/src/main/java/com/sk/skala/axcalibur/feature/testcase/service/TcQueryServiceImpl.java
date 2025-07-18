package com.sk.skala.axcalibur.feature.testcase.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.testcase.dto.response.TcDetailResponse;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcParamDataDto;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.TestCaseDataEntity;
import com.sk.skala.axcalibur.global.entity.TestCaseEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.TestCaseDataRepository;
import com.sk.skala.axcalibur.global.repository.TestCaseRepository;

import lombok.RequiredArgsConstructor;

/**
 * 테스트케이스 조회 서비스 구현체
 * - TC 상세 정보 및 연관 파라미터 조회
 */
@RequiredArgsConstructor
@Service
public class TcQueryServiceImpl implements TcQueryService {
    private final TestCaseRepository testcaseRepository;
    private final TestCaseDataRepository testcaseDataRepository;
    
    // TC 상세 정보 조회 함수
    @Override
    @Transactional(readOnly = true)
    public TcDetailResponse getTestcaseDetail(String testcaseId) {
        // 1. 테스트케이스 단건 조회
        TestCaseEntity tc = testcaseRepository.findByTestcaseId(testcaseId)
            .orElseThrow(() -> new BusinessExceptionHandler("TC ID가 존재하지 않습니다.", ErrorCode.NOT_FOUND_ERROR));

        // 2. 관련된 테스트 데이터 모두 조회 + 연관 Entity 포함 (category/context)
        List<TestCaseDataEntity> dataList = testcaseDataRepository.findAllWithCategoryAndContextByTestcaseId(tc.getId());

        // 3. DTO 변환
        List<TcParamDataDto> testDataList = dataList.stream()
            .map(data -> TcParamDataDto.builder()
                .paramId(data.getParameterKey().getId())
                .category(data.getParameterKey().getCategoryKey().getName())  // key → name
                .koName(data.getParameterKey().getNameKo())
                .name(data.getParameterKey().getName())
                .context(data.getParameterKey().getContextKey().getName())    // key → name
                .type(data.getParameterKey().getDataType())
                .length(data.getParameterKey().getLength())
                .format(data.getParameterKey().getFormat())
                .defaultValue(data.getParameterKey().getDefaultValue())
                .required(data.getParameterKey().getRequired())
                .parent(data.getParameterKey().getParentKey() != null
                        ? data.getParameterKey().getParentKey().getName()
                        : null)
                .desc(data.getParameterKey().getDescription())
                .value(data.getValue())  // 실제 TC 입력값
                .build()
            ).collect(Collectors.toList());

        // 4. 응답 DTO 조립
        return TcDetailResponse.builder()
            .tcId(tc.getTestcaseId())
            .precondition(tc.getPrecondition())
            .description(tc.getDescription())
            .expectedResult(tc.getExpected())
            .status(tc.getStatus())
            .testDataList(testDataList)
            .build();
    }
}
