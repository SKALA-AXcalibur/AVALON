package com.sk.skala.axcalibur.feature.testcase.repository;

import static com.sk.skala.axcalibur.global.entity.QApiListEntity.*;
import static com.sk.skala.axcalibur.global.entity.QMappingEntity.*;
import static com.sk.skala.axcalibur.global.entity.QScenarioEntity.*;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiListDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MappingRepositoryImpl implements MappingRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ApiListDto> findApiListByScenarioId(String scenarioId) {
        return queryFactory
            .select(Projections.constructor(ApiListDto.class, apiListEntity.apiListId, apiListEntity.name))
            .from(mappingEntity)
            .join(mappingEntity.apiListKey, apiListEntity)
            .join(mappingEntity.scenarioKey, scenarioEntity)
            .where(scenarioEntity.scenarioId.eq(scenarioId))
            .fetch();
    }
}
