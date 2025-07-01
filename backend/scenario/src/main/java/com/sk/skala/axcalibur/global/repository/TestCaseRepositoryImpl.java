package com.sk.skala.axcalibur.global.repository;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sk.skala.axcalibur.global.entity.QTestCaseEntity;
import com.sk.skala.axcalibur.global.entity.QMappingEntity;
import com.sk.skala.axcalibur.global.entity.QProjectEntity;
import com.sk.skala.axcalibur.global.entity.QScenarioEntity;
import com.sk.skala.axcalibur.global.entity.TestCaseEntity;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class TestCaseRepositoryImpl implements TestCaseRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<String> findAllByScenarioId(String scenarioId, Pageable pageable) {
        QTestCaseEntity tc = QTestCaseEntity.testCaseEntity;

        JPQLQuery<String> query = queryFactory
            .select(tc.testcaseId)
            .from(tc)
            .join(tc.mappingKey.scenarioKey)
            .where(tc.mappingKey.scenarioKey.scenarioId.eq(scenarioId));

        List<String> result = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return PageableExecutionUtils.getPage(result, pageable, query::fetchCount);
    }

    @Override
    public Optional<TestCaseEntity> findWithProjectByTestcaseId(String testcaseId) {
        QTestCaseEntity tc = QTestCaseEntity.testCaseEntity;
        QMappingEntity m = QMappingEntity.mappingEntity;
        QScenarioEntity s = QScenarioEntity.scenarioEntity;
        QProjectEntity p = QProjectEntity.projectEntity;

        TestCaseEntity result = queryFactory
            .selectFrom(tc)
            .join(tc.mappingKey, m).fetchJoin()
            .join(m.scenarioKey, s).fetchJoin()
            .join(s.project, p).fetchJoin()
            .where(tc.testcaseId.eq(testcaseId))
            .fetchOne();

        return Optional.ofNullable(result);
    }
}
