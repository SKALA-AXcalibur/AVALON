package com.sk.skala.axcalibur.apitest.feature.service;

import com.sk.skala.axcalibur.apitest.feature.code.StreamConstants;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.stream.ObjectRecord;
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
@Slf4j
public class RedisStreamListener implements StreamListener<String, ObjectRecord<String, ApiTaskDto>> {

  private final RestClient rest;
  private final RedisTemplate<String, ApiTaskDto> redis;

  public RedisStreamListener(@Qualifier("apiTaskRedisTemplate") RedisTemplate<String, ApiTaskDto> redis) {
    this.rest = RestClient.create();
    this.redis = redis;
  }

  @Override
  public void onMessage(ObjectRecord<String, ApiTaskDto> message) {
    ApiTaskDto dto = message.getValue();
    log.info("RedisStreamListener.onMessage: Received message from Redis Stream: {} on {}", dto.resultId(), Thread.currentThread().getName());

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

      log.debug("RedisStreamListener.onMessage: API response status code: {}", res.getStatusCode());
      log.debug("RedisStreamListener.onMessage: API response time: {:.3f} ms", responseTimeMs);

      // 메시지 처리 후 ACK를 보내면 메시지가 스트림에서 제거됨
      redis.opsForStream().acknowledge(StreamConstants.GROUP_NAME, message);
      log.debug("RedisStreamListener.onMessage: Message acknowledged: {}", message.getId());

      // TODO: 응답 결과 비교
      // TODO: 응답 결과 DB 저장




    } catch (ResourceAccessException e) {
      log.error("RedisStreamListener.onMessage: Resource access error: {}", e.getMessage(), e);
      // ACK를 보내지 않으면 메시지는 다른 소비자에게 재할당됨
    } catch (UnknownContentTypeException e) {
      log.error("RedisStreamListener.onMessage: Unknown content type: {}", e.getMessage(), e);
      // ACK를 보내지 않으면 메시지는 다른 소비자에게 재할당됨
    } catch (RestClientResponseException e) {
      log.error("RedisStreamListener.onMessage: API call failed with status code: {}",
          e.getStatusCode());
      // ACK를 보내지 않으면 메시지는 다른 소비자에게 재할당됨
    } catch (RestClientException e) {
      log.error("RedisStreamListener.onMessage: Error during API call: {}", e.getMessage(), e);
      // ACK를 보내지 않으면 메시지는 다른 소비자에게 재할당됨
    } catch (IllegalArgumentException e) {
      log.error("RedisStreamListener.onMessage: Invalid argument: {} (method: {})", e.getMessage(), dto.method(), e);
      // ACK를 보내지 않으면 메시지는 다른 소비자에게 재할당됨
    } catch (Exception e) {
      log.error("RedisStreamListener.onMessage: Error processing message: {}", e.getMessage(), e);
      // ACK를 보내지 않으면 메시지는 다른 소비자에게 재할당됨
    }
  }
}
