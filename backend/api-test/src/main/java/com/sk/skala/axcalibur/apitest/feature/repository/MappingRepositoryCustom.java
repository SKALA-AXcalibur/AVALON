package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.dto.response.ApiTestExecutionDataDto;
import java.util.List;

public interface MappingRepositoryCustom {

    /**
     * 프로젝트 키와 시나리오 ID 목록으로 API 테스트 실행에 필요한 모든 데이터를 한 번에 조회합니다.
     * 
     * @param projectKey  프로젝트 키
     * @param scenarioIds 시나리오 ID 목록
     * @return API 테스트 실행 데이터 목록
     */
    List<ApiTestExecutionDataDto> findExecutionDataByProjectAndScenarios(
            Integer projectKey, List<String> scenarioIds);
}
