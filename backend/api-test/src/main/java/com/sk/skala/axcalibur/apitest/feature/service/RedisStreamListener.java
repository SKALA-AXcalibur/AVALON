package com.sk.skala.axcalibur.apitest.feature.service;

import com.sk.skala.axcalibur.apitest.feature.code.StreamConstants;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import com.sk.skala.axcalibur.apitest.feature.util.ApiTaskDtoConverter;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.UnknownContentTypeException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

  private final RestClient rest;
  private final RedisTemplate<String, Object> redis;

  @Override
  public void onMessage(MapRecord<String, String, String> message) {
    Map<String, String> value = message.getValue();
    log.info("RedisStreamListener.onMessage: Raw message value: {}", value);

    try {
      ApiTaskDto dto = ApiTaskDtoConverter.fromMap(value);
      log.info("RedisStreamListener.onMessage: Successfully converted to DTO: {}", dto.resultId());
    } catch (Exception e) {
      log.error("RedisStreamListener.onMessage: Failed to convert message: {}", e.getMessage(), e);
      return; // 변환 실패시 조기 종료
    }

    ApiTaskDto dto = ApiTaskDtoConverter.fromMap(value);
    log.info("RedisStreamListener.onMessage: Received message from Redis Stream: {} on {}", dto.resultId(),
        Thread.currentThread().getName());

    try {
      // 실제 API 호출 로직
      log.debug("RedisStreamListener.onMessage: Calling API: {}", dto.uri());
      var method = HttpMethod.valueOf(dto.method());

      // 응답 시간 측정 시작 (나노초 단위로 더 정밀하게 측정)
      long startTime = System.nanoTime();

      // RestClient 요청 생성
      var requestSpec = rest.method(method)
          .uri(dto.uri())
          .headers(headers -> {
            if (dto.reqHeader() != null) {
              headers.putAll(dto.reqHeader());
            }
          });

      // 본문이 null이 아닌 경우에만 body 설정
      if (dto.reqBody() != null) {
        requestSpec = requestSpec.body(dto.reqBody());
      }

      // 요청 실행
      var res = requestSpec.retrieve()
          .toEntity(Map.class);

      // 응답 시간 측정 종료 및 계산 (나노초에서 밀리초로 변환)
      long endTime = System.nanoTime();
      double responseTimeMs = (endTime - startTime) / 1_000_000.0;

      log.info("RedisStreamListener.onMessage: API response status code: {}", res.getStatusCode());
      log.info("RedisStreamListener.onMessage: API response time: {} ms", responseTimeMs);

      // 메시지 처리 후 ACK를 보내면 메시지가 스트림에서 제거됨
      redis.opsForStream().acknowledge(StreamConstants.GROUP_NAME, message);
      log.info("RedisStreamListener.onMessage: Message acknowledged: {}", message.getId());

      // TODO: 응답 결과 비교
      // TODO: 응답 결과 DB 저장

    } catch (ResourceAccessException e) {
      log.error("RedisStreamListener.onMessage.ResourceAccessException: Resource access error: {} (method: {}) {}",
          e.getMessage(),
          dto.method(), e);
      // ACK를 보내지 않으면 메시지는 다른 소비자에게 재할당됨
      redis.opsForStream().acknowledge(StreamConstants.GROUP_NAME, message);
    } catch (UnknownContentTypeException e) {
      log.error("RedisStreamListener.onMessage.UnknownContentTypeException: Unknown content type: {} {}",
          e.getMessage(),
          e);
      // ACK를 보내지 않으면 메시지는 pending 상태로 남아 재처리 가능
    } catch (RestClientResponseException e) {
      log.error("RedisStreamListener.onMessage.RestClientResponseException: API call failed with status code: {}",
          e.getStatusCode());
      // ACK를 보내지 않으면 메시지는 pending 상태로 남아 재처리 가능
      redis.opsForStream().acknowledge(StreamConstants.GROUP_NAME, message);
    } catch (RestClientException e) {
      log.error("RedisStreamListener.onMessage.RestClientException: Error during API call: {} {}", e.getMessage(), e);
      // ACK를 보내지 않으면 메시지는 pending 상태로 남아 재처리 가능
    } catch (IllegalArgumentException e) {
      log.error("RedisStreamListener.onMessage.IllegalArgumentException: Invalid argument: {} {}", e.getMessage(),
          e);
      // ACK를 보내지 않으면 메시지는 pending 상태로 남아 재처리 가능
      redis.opsForStream().acknowledge(StreamConstants.GROUP_NAME, message);
    } catch (Exception e) {
      log.error("RedisStreamListener.onMessage.Exception: Error processing message: {} {}", e.getMessage(), e);
      // ACK를 보내지 않으면 메시지는 pending 상태로 남아 재처리 가능
    }
  }
}
