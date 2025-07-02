package com.sk.skala.axcalibur.apitest.feature.service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTestParserServiceBuildUriRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTestParserServiceParsePreconditionRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ApiTestParserActionInfoDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ApiTestParserServiceParsePreconditionResponseDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ApiTestParserSourceTargetDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ApiTestParserStepInfoDto;
import com.sk.skala.axcalibur.apitest.feature.entity.ApiTestDetailRedisEntity;
import com.sk.skala.axcalibur.apitest.feature.repository.ApiTestDetailRepository;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiTestParserServiceImpl implements ApiTestParserService {
  private static final String HEADER = "header";
  private static final String BODY = "body";
  private static final String PATH = "path";
  private static final String QUERY = "query";

  private final ApiTestDetailRepository repo;

  @Override
  public ApiTestParserServiceParsePreconditionResponseDto parsePrecondition(
      @NotNull ApiTestParserServiceParsePreconditionRequestDto dto)
      throws ParseException {
    log.info("ApiTestParserServiceImpl.parsePrecondition called");
    log.debug("dto: {}", dto);

    var resultCollections = initializeResultCollections();
    var parts = dto.precondition().split(",");

    for (String part : parts) {
      processPreconditionPart(part.trim(), dto, resultCollections);
    }

    return resultCollections;
  }

  /**
   * 결과 컬렉션들을 초기화합니다.
   */
  private ApiTestParserServiceParsePreconditionResponseDto initializeResultCollections() {
    return ApiTestParserServiceParsePreconditionResponseDto.builder()
        .path(new HashMap<>())
        .query(new LinkedMultiValueMap<>())
        .header(new LinkedMultiValueMap<>())
        .body(new HashMap<>())
        .build();
  }

  /**
   * 개별 사전조건 부분을 처리합니다.
   */
  private void processPreconditionPart(
      String part,
      ApiTestParserServiceParsePreconditionRequestDto dto,
      ApiTestParserServiceParsePreconditionResponseDto collections) throws ParseException {

    if (part.isEmpty() || !part.startsWith("step ") || !part.contains("->")) {
      log.debug("Skipping invalid precondition part: {}", part);
      return;
    }

    var stepInfo = parseStep(part);
    var actionInfo = parseAction(part, stepInfo.step());
    var entityKey = buildEntityKey(dto.scenarioKey(), stepInfo.step(), dto.statusCode());

    log.debug("Looking for entity with key: {}", entityKey);
    var entity = repo.findById(entityKey)
        .orElseThrow(() -> {
          log.error("ApiTestParserServiceImpl.parsePrecondition: Scenario not found for key: {}", entityKey);
          return new ParseException("Not found ApiTestDetailEntity: " + entityKey, 0);
        });

    mapDataBySourceType(entity, actionInfo, collections, entityKey);
  }

  /**
   * 스텝 정보를 파싱합니다.
   */
  private ApiTestParserStepInfoDto parseStep(String part) throws ParseException {
    var steps = part.substring(5).split(":");
    try {
      int step = Integer.parseInt(steps[0]);
      if (step < 1) {
        throw new NumberFormatException();
      }
      return ApiTestParserStepInfoDto.builder()
          .step(step)
          .action(steps[1].trim())
          .build();
    } catch (NumberFormatException e) {
      log.error("ApiTestParserServiceImpl.parsePrecondition: Invalid step: {}", steps[0]);
      throw new ParseException("Invalid step: " + steps[0], 0);
    }
  }

  /**
   * 액션 정보를 파싱합니다.
   */
  private ApiTestParserActionInfoDto parseAction(String part, int step) throws ParseException {
    var stepActionPart = part.substring(5 + String.valueOf(step).length() + 1).trim();
    var action = stepActionPart.split("->");

    if (action.length != 2) {
      log.error("ApiTestParserServiceImpl.parsePrecondition: Invalid action format: {}", part);
      throw new ParseException("Invalid action format: " + part, 0);
    }

    var prev = action[0].trim().split("\\|");
    var next = action[1].trim().split("\\|");

    if (prev.length < 2 || next.length < 2) {
      log.error("ApiTestParserServiceImpl.parsePrecondition: Invalid prev, next format: {}, {}", prev.length,
          next.length);
      throw new ParseException("Invalid prev, next format: " + prev.length + ", " + next.length, 0);
    }

    return ApiTestParserActionInfoDto.builder()
        .source(ApiTestParserSourceTargetDto.builder()
            .type(prev[0])
            .key(prev[1])
            .build())
        .target(ApiTestParserSourceTargetDto.builder()
            .type(next[0])
            .key(next[1])
            .build())
        .build();
  }

  /**
   * 엔티티 키를 생성합니다.
   */
  private String buildEntityKey(Integer scenarioKey, int step, Integer statusCode) {
    return scenarioKey + "-" + step + "-" + statusCode;
  }

  /**
   * 소스 타입에 따라 데이터를 매핑합니다.
   */
  private void mapDataBySourceType(
      ApiTestDetailRedisEntity entity,
      ApiTestParserActionInfoDto actionInfo,
      ApiTestParserServiceParsePreconditionResponseDto collections,
      String entityKey) {

    var sourceType = actionInfo.source().type();
    var sourceKey = actionInfo.source().key();
    var targetType = actionInfo.target().type();
    var targetKey = actionInfo.target().key();

    if (sourceType.equalsIgnoreCase(HEADER)) {
      mapFromHeader(entity, sourceKey, targetType, targetKey, collections, entityKey);
    } else if (sourceType.equalsIgnoreCase(BODY)) {
      mapFromBody(entity, sourceKey, targetType, targetKey, collections, entityKey);
    } else if (sourceType.equalsIgnoreCase(PATH)) {
      mapFromPath(entity, sourceKey, targetType, targetKey, collections, entityKey);
    } else if (sourceType.equalsIgnoreCase(QUERY)) {
      mapFromQuery(entity, sourceKey, targetType, targetKey, collections, entityKey);
    }
  }

  /**
   * Header에서 데이터를 매핑합니다.
   */
  private void mapFromHeader(
      ApiTestDetailRedisEntity entity,
      String sourceKey,
      String targetType,
      String targetKey,
      ApiTestParserServiceParsePreconditionResponseDto collections,
      String entityKey) {

    log.debug("Processing HEADER source");
    var content = entity.getHeader();
    log.debug("Header content: {}", content);

    if (content == null || content.isEmpty()) {
      log.debug("ApiTestParserServiceImpl.parsePrecondition: Header content is empty for key: {}", entityKey);
      return;
    }

    var data = content.get(sourceKey);
    log.debug("Data from header key '{}': {}", sourceKey, data);

    if (data != null && !data.isEmpty()) {
      log.debug("Processing non-null header data to target: {}", targetType);
      addDataToTarget(data, targetType, targetKey, collections);
    } else {
      log.debug("ApiTestParserServiceImpl.parsePrecondition: Header key '{}' not found for key: {}", sourceKey,
          entityKey);
    }
  }

  /**
   * Body에서 데이터를 매핑합니다.
   */
  private void mapFromBody(
      ApiTestDetailRedisEntity entity,
      String sourceKey,
      String targetType,
      String targetKey,
      ApiTestParserServiceParsePreconditionResponseDto collections,
      String entityKey) {

    var content = entity.getBody(); // Map<String, Object>
    if (content == null || content.isEmpty()) {
      log.debug("ApiTestParserServiceImpl.parsePrecondition: Body content is empty for key: {}", entityKey);
      return;
    }

    var data = content.get(sourceKey);
    if (data != null) {
      addObjectDataToTarget(data, targetType, targetKey, collections);
    } else {
      log.debug("ApiTestParserServiceImpl.parsePrecondition: Body key '{}' not found for key: {}", sourceKey,
          entityKey);
    }
  }

  /**
   * Path에서 데이터를 매핑합니다.
   */
  private void mapFromPath(
      ApiTestDetailRedisEntity entity,
      String sourceKey,
      String targetType,
      String targetKey,
      ApiTestParserServiceParsePreconditionResponseDto collections,
      String entityKey) {

    var content = entity.getPath(); // Map<String, String>
    if (content == null || content.isEmpty()) {
      log.debug("ApiTestParserServiceImpl.parsePrecondition: Path content is empty for key: {}", entityKey);
      return;
    }

    var data = content.get(sourceKey);
    if (data != null) {
      addStringDataToTarget(data, targetType, targetKey, collections);
    } else {
      log.debug("ApiTestParserServiceImpl.parsePrecondition: Path key '{}' not found for key: {}", sourceKey,
          entityKey);
    }
  }

  /**
   * Query에서 데이터를 매핑합니다.
   */
  private void mapFromQuery(
      ApiTestDetailRedisEntity entity,
      String sourceKey,
      String targetType,
      String targetKey,
      ApiTestParserServiceParsePreconditionResponseDto collections,
      String entityKey) {

    var content = entity.getQuery(); // MultiValueMap<String, String>
    if (content == null || content.isEmpty()) {
      log.debug("ApiTestParserServiceImpl.parsePrecondition: Query content is empty for key: {}", entityKey);
      return;
    }

    var data = content.get(sourceKey);
    if (data != null && !data.isEmpty()) {
      addDataToTarget(data, targetType, targetKey, collections);
    } else {
      log.debug("ApiTestParserServiceImpl.parsePrecondition: Query key '{}' not found for key: {}", sourceKey,
          entityKey);
    }
  }

  /**
   * 리스트 데이터를 타겟에 추가합니다.
   */
  private void addDataToTarget(
      List<String> data,
      String targetType,
      String targetKey,
      ApiTestParserServiceParsePreconditionResponseDto collections) {

    if (targetType.equalsIgnoreCase(HEADER)) {
      collections.header().addAll(targetKey, data);
      log.debug("Added to header: {} -> {}", targetKey, data);
    } else if (targetType.equalsIgnoreCase(BODY)) {
      if (data.size() == 1) {
        collections.body().put(targetKey, data.get(0));
        log.debug("Added single value to body: {} -> {}", targetKey, data.get(0));
      } else {
        collections.body().put(targetKey, data);
        log.debug("Added multiple values to body: {} -> {}", targetKey, data);
      }
    } else if (targetType.equalsIgnoreCase(PATH)) {
      collections.path().put(targetKey, data.get(0));
      log.debug("Added to path: {} -> {}", targetKey, data.get(0));
    } else if (targetType.equalsIgnoreCase(QUERY)) {
      collections.query().addAll(targetKey, data);
      log.debug("Added to query: {} -> {}", targetKey, data);
    }
  }

  /**
   * Object 데이터를 타겟에 추가합니다.
   */
  private void addObjectDataToTarget(
      Object data,
      String targetType,
      String targetKey,
      ApiTestParserServiceParsePreconditionResponseDto collections) {

    if (targetType.equalsIgnoreCase(HEADER)) {
      collections.header().add(targetKey, data.toString());
    } else if (targetType.equalsIgnoreCase(BODY)) {
      collections.body().put(targetKey, data);
    } else if (targetType.equalsIgnoreCase(PATH)) {
      collections.path().put(targetKey, data.toString());
    } else if (targetType.equalsIgnoreCase(QUERY)) {
      collections.query().add(targetKey, data.toString());
    }
  }

  /**
   * String 데이터를 타겟에 추가합니다.
   */
  private void addStringDataToTarget(
      String data,
      String targetType,
      String targetKey,
      ApiTestParserServiceParsePreconditionResponseDto collections) {

    if (targetType.equalsIgnoreCase(HEADER)) {
      collections.header().add(targetKey, data);
    } else if (targetType.equalsIgnoreCase(BODY)) {
      collections.body().put(targetKey, data);
    } else if (targetType.equalsIgnoreCase(PATH)) {
      collections.path().put(targetKey, data);
    } else if (targetType.equalsIgnoreCase(QUERY)) {
      collections.query().add(targetKey, data);
    }
  }

  @Override
  public String buildUri(ApiTestParserServiceBuildUriRequestDto dto) {
    log.info("ApiTestParserServiceImpl.buildUri called");
    log.debug("dto: {}", dto);
    var uri = dto.uri();
    var path = dto.path();
    var query = dto.query();

    // Path 변수 치환
    for (var entry : path.entrySet()) {
      uri = uri.replace("{" + entry.getKey() + "}", entry.getValue());
    }

    // Query 파라미터 추가
    if (!query.isEmpty()) {
      StringBuilder queryString = new StringBuilder(uri + "?");
      for (var entry : query.entrySet()) {
        for (String value : entry.getValue()) {
          queryString.append(entry.getKey()).append("=").append(value).append("&");
        }
      }
      // 마지막 & 제거
      queryString.setLength(queryString.length() - 1);
      uri = queryString.toString();
    }

    return uri;
  }

}
