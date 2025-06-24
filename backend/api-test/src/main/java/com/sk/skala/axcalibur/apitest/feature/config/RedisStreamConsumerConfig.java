package com.sk.skala.axcalibur.apitest.feature.config;

import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.sk.skala.axcalibur.apitest.feature.code.StreamConstants;
import com.sk.skala.axcalibur.apitest.feature.service.RedisStreamListener;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

/**
 * Redis Stream 소비자(Consumer) 설정 클래스
 * Redis Stream에서 메시지를 수신하고 처리하기 위한 설정을 담당합니다.
 * 가상 스레드를 활용하여 비동기적으로 메시지를 처리합니다.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisStreamConsumerConfig {

  @Value("${spring.data.redis.stream.container.batch-size:10}")
  private final Integer batch;

  @Value("${spring.data.redis.stream.container.poll-timeout:1}")
  private final Integer pollTimeout;

  /**
   * Redis Stream에서 수신한 메시지를 처리하는 리스너
   * 실제 메시지 처리 로직이 구현되어 있습니다.
   */
  private final RedisStreamListener listener;

  /**
   * 고유한 소비자 ID 생성을 위한 UUIDv7 생성기
   * 각 소비자 인스턴스를 고유하게 식별하는데 사용됩니다.
   */
  private final TimeBasedGenerator UUIDv7;

  /**
   * Java 21의 가상 스레드를 사용하는 TaskExecutor 빈 설정
   * 가상 스레드를 사용하여 리소스 효율성을 높이고 동시성을 개선합니다.
   *
   * @return 가상 스레드를 사용하는 TaskExecutor 인스턴스
   */
  @Bean
  public TaskExecutor virtualThreadTaskExecutor() {
    SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
    executor.setVirtualThreads(true); // 가상 스레드 사용 활성화
    return executor;
  }

  /**
   * StreamMessageListenerContainer를 설정하고 실행하는 빈 설정
   * Redis Stream으로부터 메시지를 수신하고 처리하는 컨테이너를 구성합니다.
   *
   * @param connectionFactory         Redis 연결 팩토리
   * @param virtualThreadTaskExecutor 가상 스레드 기반 작업 실행기
   * @return 구성된 StreamMessageListenerContainer 인스턴스
   */
  @Bean(initMethod = "start", destroyMethod = "stop")
  public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(
      RedisConnectionFactory connectionFactory,
      TaskExecutor virtualThreadTaskExecutor) {

    // 컨테이너 옵션 설정
    var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
        .pollTimeout(Duration.ofSeconds(pollTimeout)) // 메시지가 없을 때 대기 시간 설정
        .batchSize(batch) // 한 번에 최대 10개 메시지 가져오기
        .executor(virtualThreadTaskExecutor) // 리스너 실행을 위한 실행기(가상 스레드) 주입
        .build();

    // 컨테이너 생성
    var container = StreamMessageListenerContainer.create(connectionFactory, options);

    // 컨테이너에 리스너 등록
    // 소비자 그룹과 이름을 지정하고, 아직 처리되지 않은 새 메시지부터 읽도록 설정
    var consumer = Consumer.from(StreamConstants.GROUP_NAME, "avalon-" + UUIDv7.generate().toString()); // 고유한 소비자 ID 생성
    container.receive(
        consumer, // 소비자 정보
        StreamOffset.create(
            StreamConstants.STREAM_KEY, // 스트림 키
            ReadOffset.lastConsumed()), // 마지막으로 소비된 메시지 이후부터 읽기
        this.listener // 메시지 처리 리스너
    );

    return container;
  }

}
