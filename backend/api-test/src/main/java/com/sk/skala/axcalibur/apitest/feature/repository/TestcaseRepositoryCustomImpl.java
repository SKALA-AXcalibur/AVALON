package com.sk.skala.axcalibur.apitest.feature.repository;

import static com.sk.skala.axcalibur.apitest.feature.entity.QTestcaseEntity.*;
import static com.sk.skala.axcalibur.apitest.feature.entity.QScenarioEntity.*;
import static com.sk.skala.axcalibur.apitest.feature.entity.QTestcaseResultEntity.*;
import static com.sk.skala.axcalibur.apitest.feature.entity.QMappingEntity.*;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sk.skala.axcalibur.apitest.feature.dto.response.TestcaseSuccessResponseDto;
import com.sk.skala.axcalibur.apitest.feature.entity.QTestcaseResultEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.ScenarioEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TestcaseRepositoryCustomImpl implements TestcaseRepositoryCustom {

  private final JPAQueryFactory query;

  @Override
  public List<TestcaseSuccessResponseDto> findByScenarioInWithResultSuccess(
      List<ScenarioEntity> entities) {
    log.info("TestcaseRepositoryCustomImpl.findByScenarioInWithResultSuccess() called with {} scenarios", entities.size());

    // 서브쿼리용 별칭 생성
    QTestcaseResultEntity subResult = new QTestcaseResultEntity("subResult");


    return query
        .select(Projections.constructor(
            TestcaseSuccessResponseDto.class,
            testcaseEntity.id,
            scenarioEntity.scenarioId,
            testcaseResultEntity.success
        )).from(testcaseEntity)
        .join(testcaseEntity.mapping, mappingEntity)
        .join(mappingEntity.scenario, scenarioEntity)
        .join(testcaseEntity.testcaseResults, testcaseResultEntity)
        .where(scenarioEntity.in(entities),
            testcaseResultEntity.createdAt.eq(
                // 각 testcase별로 createdAt이 최대인 레코드만 선택
                JPAExpressions
                    .select(subResult.createdAt.max())
                    .from(subResult)
                    .where(subResult.testcase.eq(testcaseEntity))
            ))
        .fetch();
  }


}





