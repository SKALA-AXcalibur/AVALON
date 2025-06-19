package com.sk.skala.axcalibur.apitest.feature.repository;

import static com.sk.skala.axcalibur.apitest.feature.entity.QTestcaseResultEntity.*;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sk.skala.axcalibur.apitest.feature.entity.QTestcaseResultEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseResultEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TestcaseResultRepositoryCustomImpl implements TestcaseResultRepositoryCustom {

  private final JPAQueryFactory query;


  @Override
  public List<TestcaseResultEntity> findLastResultByTestcaseIn(List<TestcaseEntity> entities) {
    log.info("TestcaseResultRepositoryCustomImpl.findLastResultByTestcaseIn() called with {} testcases", entities.size());

    var subResult = new QTestcaseResultEntity("subResult");

    return query
        .select(testcaseResultEntity)
        .from(testcaseResultEntity)
        .where(testcaseResultEntity.testcase.in(entities),
            testcaseResultEntity.createdAt.eq(
                // 각 testcase별로 createdAt이 최대인 레코드만 선택
                JPAExpressions
                    .select(subResult.createdAt.max())
                    .from(subResult)
                    .where(subResult.testcase.eq(testcaseResultEntity.testcase))
            ))
        .fetch();
  }
}
