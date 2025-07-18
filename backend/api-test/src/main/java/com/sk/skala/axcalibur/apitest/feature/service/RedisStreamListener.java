package com.sk.skala.axcalibur.apitest.feature.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.skala.axcalibur.apitest.feature.code.StreamConstants;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTestParserServiceBuildUriRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTestParserServiceParsePreconditionRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.TestcaseResultUpdateReasonDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.TestcaseResultUpdateResultDto;
import com.sk.skala.axcalibur.apitest.feature.entity.ApiTestDetailRedisEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseResultEntity;
import com.sk.skala.axcalibur.apitest.feature.repository.ApiTestDetailRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.ApiTestRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseResultRepository;
import com.sk.skala.axcalibur.apitest.feature.util.ApiTaskDtoConverter;
import com.sk.skala.axcalibur.apitest.global.code.ErrorCode;
import com.sk.skala.axcalibur.apitest.global.exception.BusinessExceptionHandler;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.UnknownContentTypeException;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RedisStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

  private static final int SUCCESS_CODE = 2;
  private static final int REDIRECT_CODE = 3;
  private static final int CLIENT_ERROR_CODE = 4;
  private static final int SERVER_ERROR_CODE = 5;

  @Value("${spring.data.redis.stream.listener.max-retry-count:5}")
  private Integer retry;

  private final RestClient rest;
  private final RedisTemplate<String, Object> redis;
  private final TestcaseResultRepository tr;
  private final TestcaseRepository tc;
  private final ApiTestRepository at;
  private final ApiTestDetailRepository ad;

  private final ApiTestParserService parser;
  private final ObjectMapper mapper;

  private final TypeReference<HashMap<String, Object>> objectMap = new TypeReference<HashMap<String, Object>>() {
  };
  private final TypeReference<LinkedMultiValueMap<String, String>> multiValueMap = new TypeReference<LinkedMultiValueMap<String, String>>() {
  };
  private final TypeReference<HashMap<String, String>> stringMap = new TypeReference<HashMap<String, String>>() {
  };

  @Override
  public void onMessage(MapRecord<String, String, String> message) {
    Map<String, String> value = message.getValue();
    log.info("RedisStreamListener.onMessage: Raw message value: {}", value);
    ApiTaskDto dto = ApiTaskDtoConverter.fromMap(value);
    log.info(
        "RedisStreamListener.onMessage: Received message from Redis Stream: resultId: {}, scenarioId: {}, step: {} on {}",
        dto.resultId(), dto.id(), dto.step(),
        Thread.currentThread().getName());

    // result id 확인
    var atId = dto.id();
    var atEntityOptional = at.findById(atId);
    if (atEntityOptional.isEmpty()) {
      // 이미 수행이 다 끝나 Redis에 없는 경우
      log.warn("RedisStreamListener.onMessage: No ApiTestEntity in Redis found for id: {}", atId);
      redis.opsForStream().acknowledge(StreamConstants.GROUP_NAME, message);
      return;
    } else if (atEntityOptional.get().getCompleted() + 1 < dto.step()) {
      // 현재 단계가 수행한 단계가 아닌 경우
      log.warn("RedisStreamListener.onMessage: ApiTestEntity step mismatch for id: {}, expected: {}, actual: {}",
          atId, atEntityOptional.get().getCompleted() + 1, dto.step());
      // 수행을 위해 대기 -> PendingMessageReclaimerService가 처리함
      return;
    }
    var resultId = dto.resultId();
    var adId = dto.id() + "-" + dto.step() + "-" + dto.resultId() + "-" + dto.statusCode();
    var adEntity = ad.findById(adId).orElse(null);
    if (adEntity != null) {
      // 이미 처리되었거나 처리 중인 테스트케이스인 경우
      log.warn("RedisStreamListener.onMessage: ApiTestDetailEntity already exists for id: {}", adId);
      // Redis에 저장된 adEntity가 있으면, 이미 처리 중인 테스트케이스이므로 아무 작업도 하지 않고 리턴
      return;
    }
    adEntity = ApiTestDetailRedisEntity.builder()
        .id(adId)
        .resultId(resultId)
        .header(new LinkedMultiValueMap<String, String>())
        .body(new HashMap<String, Object>())
        .path(new HashMap<String, String>())
        .query(new LinkedMultiValueMap<String, String>())
        .build();
    // Redis에 저장해 다른 소비자가 중복 처리하지 않도록 함
    log.debug("RedisStreamListener.onMessage: Saving ApiTestDetailEntity to Redis: {}", adEntity.getId());
    ad.save(adEntity); // Redis는 Transactional 필요 없음
    try {
      String uri = dto.uri();
      LinkedMultiValueMap<String, String> reqHeader = mapper.convertValue(dto.reqHeader(), multiValueMap);
      HashMap<String, Object> reqBody = mapper.convertValue(dto.reqBody(), objectMap);
      HashMap<String, String> reqPath = mapper.convertValue(dto.reqPath(), stringMap);
      LinkedMultiValueMap<String, String> reqQuery = mapper.convertValue(dto.reqQuery(), multiValueMap);

      // 사전 조건 파싱 구현
      if (dto.precondition() != null && !dto.precondition().isEmpty()) {
        log.info("RedisStreamListener.onMessage: Precondition is detected, message.id : {}", message.getId());

        // 사전 조건 파싱
        var preconditions = parser.parsePrecondition(ApiTestParserServiceParsePreconditionRequestDto.builder()
            .precondition(dto.precondition())
            .scenarioKey(dto.id())
            .statusCode(dto.statusCode())
            .build());

        // header 있으면 추출 및 병합
        if (preconditions.header() != null && !preconditions.header().isEmpty()) {
          log.debug("RedisStreamListener.onMessage: Merging precondition headers: {}", preconditions.header());
          reqHeader.addAll(preconditions.header());
        }

        // body 있으면 추출 및 병합
        if (preconditions.body() != null && !preconditions.body().isEmpty()) {
          log.debug("RedisStreamListener.onMessage: Merging precondition body: {}", preconditions.body());
          reqBody.putAll(preconditions.body());
        }

        // path 있으면 추출 및 병합
        if (preconditions.path() != null && !preconditions.path().isEmpty()) {
          log.debug("RedisStreamListener.onMessage: Merging precondition path: {}", preconditions.path());
          // reqPath와 사전조건의 path가 겹치는 경우 사전조건이 우선됨
          for (var entry : preconditions.path().entrySet()) {
            reqPath.merge(entry.getKey(), entry.getValue(), (existing, incoming) -> {
              log.debug("RedisStreamListener.onMessage: Merging precondition path entry: {}, from {} to {}",
                  entry.getKey(), incoming, existing);
              return incoming;
            });
          }

        }

        // query 있으면 추출 및 병합
        if (preconditions.query() != null && !preconditions.query().isEmpty()) {
          log.debug("RedisStreamListener.onMessage: Merging precondition query: {}", preconditions.query());
          reqQuery.addAll(preconditions.query());
        }
      }

      // URI 빌드
      uri = parser.buildUri(ApiTestParserServiceBuildUriRequestDto.builder()
          .uri(uri)
          .path(reqPath)
          .query(reqQuery)
          .build());

      // 실제 API 호출 로직
      log.debug("RedisStreamListener.onMessage: Calling API: {}", uri);
      var method = HttpMethod.valueOf(dto.method());

      // RestClient 요청 생성
      var requestSpec = rest.method(method)
          .uri(uri)
          .headers(headers -> {
            if (reqHeader != null) {
              headers.putAll(reqHeader);
            }
          });

      // 본문이 null이 아닌 경우에만 body 설정
      if (reqBody != null) {
        requestSpec = requestSpec.body(reqBody);
      }

      log.debug("RedisStreamListener.onMessage: Request method: {}, URI: {}, Headers: {}, Body: {}",
          method, uri, reqHeader, reqBody);

      // 응답 시간 측정 시작 (나노초 단위로 더 정밀하게 측정)
      long startTime = System.nanoTime();

      // 요청 실행
      var res = requestSpec.retrieve().toEntity(new ParameterizedTypeReference<Map<String, Object>>() {
      });

      // 응답 시간 측정 종료 및 계산 (나노초에서 밀리초로 변환)
      double responseTimeMs = (System.nanoTime() - startTime) / 1_000_000.0;

      log.info("RedisStreamListener.onMessage: API response status code: {}", res.getStatusCode());
      log.debug("RedisStreamListener.onMessage: API response time: {} ms", responseTimeMs);

      // status code 비교
      int status = res.getStatusCode().value() / 100; // 2XX, 3XX, 4XX, 5XX
      if (status != dto.statusCode().intValue()) {
        // 상태 코드가 예상과 다를 경우 재시도
        log.warn("RedisStreamListener.onMessage: API response status code mismatch: {} != {}",
            res.getStatusCode().value(), dto.statusCode().intValue());
        ackAndRetry(message, dto, adEntity);
        return;
      }

      LinkedMultiValueMap<String, String> header = mapper.convertValue(res.getHeaders(), multiValueMap); // never null
      HashMap<String, Object> body = mapper.convertValue(res.getBody() == null ? new HashMap<>() : res.getBody(),
          objectMap); // nullable
      Boolean success = true;

      if (method == HttpMethod.POST || method == HttpMethod.PATCH) {
        // method가 멱등성을 가지지 않은 경우(POST, PATCH), 결과 형식을 비교해 성공 여부 결정

        if (dto.resHeader() == null || dto.resHeader().isEmpty()) {
          success = true;
        } else if (header.isEmpty()) {
          log.info("RedisStreamListener.onMessage: Response header is empty, expected: {}", dto.resHeader());
          success = false;
        } else {
          // 응답 헤더가 예상과 일치하는지 확인
          for (var key : dto.resHeader().keySet()) {
            if (!header.containsKey(key)) {
              log.error("RedisStreamListener.onMessage: Expected header {} not found in response", key);
              success = false;
              break;
            }
          }
        }

        if (success) {
        } else if (dto.resBody() == null || dto.resBody().isEmpty()) {
          success = true;
        } else if (body == null || body.isEmpty()) {
          log.info("RedisStreamListener.onMessage: Response body is empty, expected: {}", dto.resBody());
          success = false;
        } else {
          // 응답 바디가 예상과 일치하는지 확인
          for (var key : dto.resBody().keySet()) {
            if (!body.containsKey(key)) {
              log.error("RedisStreamListener.onMessage: Expected body key {} not found in response", key);
              success = false;
              break;
            }
          }
        }
      } else {
        // method가 멱등성을 가지는 경우(GET, PUT, DELETE, OPTIONS, HEAD), 응답 결과를 비교하여 성공 여부 결정
        if (dto.resHeader() == null || dto.resHeader().isEmpty()) {
          success = true;
        } else if (header.isEmpty()) {
          log.info("RedisStreamListener.onMessage: Response header is empty, expected: {}", dto.resHeader());
          success = false;
        } else {
          // 응답 헤더가 예상과 일치하는지 확인
          for (var entry : dto.resHeader().entrySet()) {
            if (!header.containsKey(entry.getKey()) || !header.get(entry.getKey()).containsAll(entry.getValue())) {
              log.error("RedisStreamListener.onMessage: Expected header {} with values {} not found in response",
                  entry.getKey(), entry.getValue());
              success = false;
              break;
            }
          }
        }

        if (success) {
        } else if (dto.resBody() == null || dto.resBody().isEmpty()) {
          success = true;
        } else if (body == null || body.isEmpty()) {
          log.info("RedisStreamListener.onMessage: Response body is empty, expected: {}", dto.resBody());
          success = false;
        } else {
          // 응답 바디가 예상과 일치하는지 확인
          for (var entry : dto.resBody().entrySet()) {
            if (!body.containsKey(entry.getKey()) || !body.get(entry.getKey()).equals(entry.getValue())) {
              log.error("RedisStreamListener.onMessage: Expected body key {} with value {} not found in response",
                  entry.getKey(), entry.getValue());
              success = false;
              break;
            }
          }
        }

      }

      // RDB에 성공 여부와 응답 저장
      var trEntity = tr.findById(dto.resultId()).orElseGet(() -> {
        log.error("RedisStreamListener.onMessage: No TestcaseResultEntity found for resultId: {}", dto.resultId());
        return TestcaseResultEntity.builder()
            .id(dto.resultId())
            .testcase(tc.findById(dto.testcaseId())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.UNVALID_TESTCASE_ERROR)))
            .result("FAILED")
            .success(false)
            .time(responseTimeMs)
            .build();
      });
      var updated = TestcaseResultUpdateResultDto.builder()
          .header(header)
          .body(body)
          .success(success)
          .time(responseTimeMs)
          .build();
      tr.updateResult(trEntity, updated);
      log.debug("RedisStreamListener.onMessage: TestcaseResultEntity updated: resultId={}, success={}, time={} ms",
          dto.resultId(), success, responseTimeMs);

      // redis에 완료 단계 업데이트
      var atEntity = atEntityOptional.get();
      if (atEntity.getFinish() - atEntity.getCompleted() <= 1) {
        // 마지막 단계인 경우
        at.delete(atEntity);
      } else {
        var nextAtEntity = atEntity.toBuilder()
            .completed(atEntity.getCompleted() + 1)
            .build();
        at.save(nextAtEntity);
      }
      log.debug("RedisStreamListener.onMessage: ApiTestEntity updated: id={}, completed={}",
          atEntity.getId(), atEntity.getCompleted());

      var nextAdEntity = adEntity.toBuilder()
          .header(header)
          .body(body)
          .path(reqPath)
          .query(reqQuery)
          .build();
      ad.save(nextAdEntity);
      log.debug("RedisStreamListener.onMessage: ApiTestDetailEntity updated: id={}", nextAdEntity.getId());

      // 메시지 처리 후 ACK를 보내면 메시지가 스트림에서 제거됨
      redis.opsForStream().acknowledge(StreamConstants.GROUP_NAME, message);
      log.info("RedisStreamListener.onMessage: Message acknowledged: {}", message.getId());

    } catch (ResourceAccessException e) {
      log.error("RedisStreamListener.onMessage.ResourceAccessException: Resource access error: {} (method: {}) {}",
          e.getMessage(),
          dto.method(), e);
      ackAndRetry(message, dto, adEntity);
    } catch (UnknownContentTypeException e) {
      log.error("RedisStreamListener.onMessage.UnknownContentTypeException: Unknown content type: {} {}",
          e.getMessage(),
          e);
      ackAndRetry(message, dto, adEntity);
    } catch (RestClientResponseException e) {
      log.error("RedisStreamListener.onMessage.RestClientResponseException: API call failed with status code: {}",
          e.getStatusCode());
      ackAndRetry(message, dto, adEntity);
    } catch (RestClientException e) {
      log.error("RedisStreamListener.onMessage.RestClientException: Error during API call: {} {}", e.getMessage(), e);
      ackAndRetry(message, dto, adEntity);
    } catch (ParseException e) {
      log.error("RedisStreamListener.onMessage.ParseException: Parse exception occurred: {} {}", e.getMessage(), e);
      // ack 하고 DB에 실패 이유 저장
      redis.opsForStream().acknowledge(StreamConstants.GROUP_NAME, message);
      // DB에 실패 이유 저장
      var reason = e.getMessage() != null ? e.getMessage() : "Parse error occurred";
      Boolean success = false;
      var trEntity = tr.findById(dto.resultId()).orElseGet(() -> {
        log.error("RedisStreamListener.onMessage: No TestcaseResultEntity found for resultId: {}", dto.resultId());
        return TestcaseResultEntity.builder()
            .id(dto.resultId())
            .testcase(tc.findById(dto.testcaseId())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.UNVALID_TESTCASE_ERROR)))
            .build();
      });
      tr.updateReason(trEntity, TestcaseResultUpdateReasonDto.builder()
          .success(success)
          .reason(reason)
          .build());
    } catch (IllegalArgumentException e) {
      log.error("RedisStreamListener.onMessage.IllegalArgumentException: Invalid argument: {} {}", e.getMessage(),
          e);
      ackAndRetry(message, dto, adEntity);
    } catch (Exception e) {
      log.error("RedisStreamListener.onMessage.Exception: Error processing message: {} {}", e.getMessage(), e);
      ackAndRetry(message, dto, adEntity);
    }
  }

  /**
   * 메시지를 ACK하고 재시도합니다.
   *
   * @param message  Redis Stream에서 받은 메시지
   * @param dto      ApiTaskDto 객체
   * @param adEntity ApiTestDetailRedisEntity 객체
   */
  private void ackAndRetry(MapRecord<String, String, String> message, ApiTaskDto dto,
      ApiTestDetailRedisEntity adEntity) {
    redis.opsForStream().acknowledge(StreamConstants.GROUP_NAME, message);
    ad.delete(adEntity);
    if (dto.attempt() >= retry) {
      log.warn("RedisStreamListener.ackAndRetry: Max retry attempts reached for message: {}. Giving up.",
          message.getId());
      return;
    }
    var next = dto.toBuilder()
        .attempt(dto.attempt() + 1)
        .build();
    log.info("RedisStreamListener.ackAndRetry: Retrying message: {} (attempt {}/{})", message.getId(), next.attempt(),
        retry);
    // 재시도 로직을 위해 스트림에 다시 추가
    redis.opsForStream().add(StreamConstants.STREAM_KEY, ApiTaskDtoConverter.toMap(next));
  }
}
