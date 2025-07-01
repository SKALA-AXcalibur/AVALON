package com.sk.skala.axcalibur.apitest.feature.repository;

import static com.sk.skala.axcalibur.apitest.feature.entity.QTestcaseResultEntity.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sk.skala.axcalibur.apitest.feature.dto.request.TestcaseResultUpdateReasonDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.TestcaseResultUpdateResultDto;
import com.sk.skala.axcalibur.apitest.feature.entity.QTestcaseResultEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseResultEntity;
import com.sk.skala.axcalibur.apitest.global.code.ErrorCode;
import com.sk.skala.axcalibur.apitest.global.exception.BusinessExceptionHandler;

import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TestcaseResultRepositoryCustomImpl implements TestcaseResultRepositoryCustom {

  private final JPAQueryFactory query;
  private final ObjectMapper mapper;

  @Override
  public List<TestcaseResultEntity> findLastResultByTestcaseIn(List<TestcaseEntity> entities) {
    log.info("TestcaseResultRepositoryCustomImpl.findLastResultByTestcaseIn: called with {} testcases",
        entities.size());

    var subResult = new QTestcaseResultEntity("subResult");

    return query
        .selectFrom(testcaseResultEntity)
        .where(testcaseResultEntity.testcase.in(entities),
            testcaseResultEntity.createdAt.eq(
                // 각 testcase별로 createdAt이 최대인 레코드만 선택
                JPAExpressions
                    .select(subResult.createdAt.max())
                    .from(subResult)
                    .where(subResult.testcase.eq(testcaseResultEntity.testcase))))
        .fetch();
  }

  @Override
  public TestcaseResultEntity updateResult(TestcaseResultEntity entity, TestcaseResultUpdateResultDto dto) {
    log.info("TestcaseResultRepositoryCustomImpl.update: called for entity with id: {}", entity.getId());
    var result = "";
    var map = new HashMap<String, Object>();
    map.put("header", dto.header() == null ? new HashMap<>() : dto.header());
    map.put("body", dto.body() == null ? new HashMap<>() : dto.body());

    try {
      result = mapper.writeValueAsString(map);
    } catch (JsonProcessingException e) {
      log.error("TestcaseResultRepositoryCustomImpl.update: Error serializing result to JSON: {}", e.getMessage());
      throw new BusinessExceptionHandler("Error serializing result to JSON", ErrorCode.JACKSON_PROCESS_ERROR, e);
    }

    query.update(testcaseResultEntity)
        .where(testcaseResultEntity.id.eq(entity.getId()))
        .set(testcaseResultEntity.result, result)
        .set(testcaseResultEntity.success, dto.success())
        .set(testcaseResultEntity.time, dto.time())
        .execute();

    return query.selectFrom(testcaseResultEntity)
        .where(testcaseResultEntity.id.eq(entity.getId()))
        .fetchOne();
  }

  @Override
  public TestcaseResultEntity updateReason(TestcaseResultEntity entity, TestcaseResultUpdateReasonDto dto) {
    log.info("TestcaseResultRepositoryCustomImpl.updateReason: called for entity with id: {}", entity.getId());

    query.update(testcaseResultEntity)
        .where(testcaseResultEntity.id.eq(entity.getId()))
        .set(testcaseResultEntity.reason, dto.reason())
        .set(testcaseResultEntity.success, dto.success())
        .execute();

    return query.selectFrom(testcaseResultEntity)
        .where(testcaseResultEntity.id.eq(entity.getId()))
        .fetchOne();
  }
}
