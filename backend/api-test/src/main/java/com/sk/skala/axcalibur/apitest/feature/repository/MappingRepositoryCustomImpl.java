package com.sk.skala.axcalibur.apitest.feature.repository;

import static com.sk.skala.axcalibur.apitest.feature.entity.QMappingEntity.*;
import static com.sk.skala.axcalibur.apitest.feature.entity.QTestcaseEntity.*;
import static com.sk.skala.axcalibur.apitest.feature.entity.QScenarioEntity.*;
import static com.sk.skala.axcalibur.apitest.feature.entity.QApiListEntity.*;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ApiTestExecutionDataDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MappingRepositoryCustomImpl implements MappingRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<ApiTestExecutionDataDto> findExecutionDataByProjectAndScenarios(
            Integer projectKey, List<String> scenarioIds) {
        log.info(
                "MappingRepositoryCustomImpl.findExecutionDataByProjectAndScenarios: called with project: {}, scenarios: {}",
                projectKey, scenarioIds);

        return query
                .select(Projections.constructor(
                        ApiTestExecutionDataDto.class,
                        mappingEntity.id,
                        mappingEntity.step,
                        testcaseEntity.id,
                        testcaseEntity.testcaseId,
                        testcaseEntity.precondition,
                        testcaseEntity.status,
                        apiListEntity.id,
                        apiListEntity.method,
                        apiListEntity.url,
                        apiListEntity.path))
                .from(mappingEntity)
                .join(mappingEntity.scenario, scenarioEntity)
                .join(mappingEntity.apiList, apiListEntity)
                .join(mappingEntity.testcases, testcaseEntity)
                .where(scenarioEntity.projectKey.eq(projectKey)
                        .and(scenarioEntity.scenarioId.in(scenarioIds)))
                .orderBy(mappingEntity.step.asc(), testcaseEntity.id.asc())
                .fetch();
    }
}
