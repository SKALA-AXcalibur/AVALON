package com.sk.skala.axcalibur.global.repository;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sk.skala.axcalibur.global.entity.QTestCaseDataEntity;
import com.sk.skala.axcalibur.global.entity.QCategoryEntity;
import com.sk.skala.axcalibur.global.entity.QContextEntity;
import com.sk.skala.axcalibur.global.entity.QParameterEntity;
import com.sk.skala.axcalibur.global.entity.TestCaseDataEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestCaseDataRepositoryImpl implements TestCaseDataRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<TestCaseDataEntity> findAllWithCategoryAndContextByTestcaseId(Integer testcaseId) {
        QTestCaseDataEntity td = QTestCaseDataEntity.testCaseDataEntity;
        QParameterEntity p = QParameterEntity.parameterEntity;
        QCategoryEntity c = QCategoryEntity.categoryEntity;
        QContextEntity ctx = QContextEntity.contextEntity;

        return queryFactory
            .selectFrom(td)
            .join(td.parameterKey, p).fetchJoin()
            .join(p.categoryKey, c).fetchJoin()
            .join(p.contextKey, ctx).fetchJoin()
            .where(td.testcaseKey.id.eq(testcaseId))
            .fetch();
    }

    @Override
    public List<TestCaseDataEntity> findAllWithParameterByTestcaseId(Integer testcaseId) {
        QTestCaseDataEntity td = QTestCaseDataEntity.testCaseDataEntity;
        QParameterEntity p = QParameterEntity.parameterEntity;

        return queryFactory
            .selectFrom(td)
            .join(td.parameterKey, p).fetchJoin()
            .where(td.testcaseKey.id.eq(testcaseId))
            .fetch();
    }
}
