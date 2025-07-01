package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.dto.response.ParameterWithDataDto;
import java.util.List;

public interface ParameterRepositoryCustom {

    /**
     * API 목록과 테스트케이스 ID 목록으로 파라미터와 테스트케이스 데이터를 한 번에 조회합니다.
     * 
     * @param apiListIds  API 목록 ID들
     * @param testcaseIds 테스트케이스 ID들
     * @return 파라미터와 테스트케이스 데이터 목록
     */
    List<ParameterWithDataDto> findParametersWithDataByApiListAndTestcase(
            List<Integer> apiListIds, List<Integer> testcaseIds);
}
