package com.sk.skala.axcalibur.apitest.feature.repository;

import static com.sk.skala.axcalibur.apitest.feature.entity.QParameterEntity.*;
import static com.sk.skala.axcalibur.apitest.feature.entity.QTestcaseDataEntity.*;
import static com.sk.skala.axcalibur.apitest.feature.entity.QCategoryEntity.*;
import static com.sk.skala.axcalibur.apitest.feature.entity.QContextEntity.*;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ParameterWithDataDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ParameterRepositoryCustomImpl implements ParameterRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<ParameterWithDataDto> findParametersWithDataByApiListAndTestcase(
            List<Integer> apiListIds, List<Integer> testcaseIds) {
        log.info(
                "ParameterRepositoryCustomImpl.findParametersWithDataByApiListAndTestcase: called with apiListIds: {}, testcaseIds: {}",
                apiListIds.size(), testcaseIds.size());

        return query
                .select(Projections.constructor(
                        ParameterWithDataDto.class,
                        parameterEntity.id,
                        parameterEntity.name,
                        parameterEntity.dataType,
                        parameterEntity.apiList.id,
                        categoryEntity.name,
                        contextEntity.name,
                        parameterEntity.parent.id,
                        testcaseDataEntity.testcase.id,
                        testcaseDataEntity.value))
                .from(parameterEntity)
                .join(parameterEntity.category, categoryEntity)
                .join(parameterEntity.context, contextEntity)
                .leftJoin(testcaseDataEntity).on(testcaseDataEntity.parameter.eq(parameterEntity)
                        .and(testcaseDataEntity.testcase.id.in(testcaseIds)))
                .where(parameterEntity.apiList.id.in(apiListIds))
                .orderBy(parameterEntity.apiList.id.asc(),
                        parameterEntity.parent.id.asc().nullsFirst(),
                        parameterEntity.id.asc())
                .fetch();
    }
}
