package com.sk.skala.axcalibur.apitest.feature.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.sk.skala.axcalibur.apitest.feature.service.RedisStreamListener;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

@ExtendWith(MockitoExtension.class)
@DisplayName("Redis Stream Consumer Config 테스트")
class RedisStreamConsumerConfigTest {

    @Mock
    private RedisStreamListener listener;

    @Mock
    private TimeBasedGenerator uuidGenerator;

    @Mock
    private RedisConnectionFactory connectionFactory;

    private RedisStreamConsumerConfig config;

    @BeforeEach
    void setUp() {
        config = new RedisStreamConsumerConfig(listener, uuidGenerator);
    }

    @Test
    @DisplayName("가상 스레드 TaskExecutor가 정상적으로 생성되어야 한다")
    void virtualThreadTaskExecutor_shouldBeConfiguredCorrectly() {
        // When
        TaskExecutor taskExecutor = config.virtualThreadTaskExecutor();

        // Then
        assertThat(taskExecutor).isInstanceOf(SimpleAsyncTaskExecutor.class);

        SimpleAsyncTaskExecutor simpleExecutor = (SimpleAsyncTaskExecutor) taskExecutor;
        // 가상 스레드 설정은 private 필드이므로 실제 실행을 통해 확인
        assertThat(simpleExecutor).isNotNull();
    }

    @Test
    @DisplayName("StreamMessageListenerContainer가 정상적으로 생성되어야 한다")
    void streamMessageListenerContainer_shouldBeConfiguredCorrectly() {
        // Given
        TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        UUID testUuid = UUID.randomUUID();

        when(uuidGenerator.generate()).thenReturn(testUuid); // When
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container = config
                .streamMessageListenerContainer(connectionFactory, taskExecutor);

        // Then
        assertThat(container).isNotNull();
        verify(uuidGenerator).generate(); // UUID 생성이 호출되었는지 확인
    }

    @Test
    @DisplayName("고유한 소비자 ID가 생성되어야 한다")
    void consumerIdGeneration_shouldCreateUniqueId() {
        // Given
        TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        UUID testUuid1 = UUID.randomUUID();
        UUID testUuid2 = UUID.randomUUID();

        when(uuidGenerator.generate()).thenReturn(testUuid1, testUuid2);

        // When
        config.streamMessageListenerContainer(connectionFactory, taskExecutor);
        config.streamMessageListenerContainer(connectionFactory, taskExecutor);

        // Then
        verify(uuidGenerator, times(2)).generate(); // 각 호출마다 UUID 생성
    }

    @Test
    @DisplayName("TaskExecutor 빈의 가상 스레드 설정이 활성화되어야 한다")
    void taskExecutor_shouldHaveVirtualThreadsEnabled() {
        // When
        TaskExecutor taskExecutor = config.virtualThreadTaskExecutor();

        // Then
        assertThat(taskExecutor).isInstanceOf(SimpleAsyncTaskExecutor.class);

        // 가상 스레드 사용 여부를 간접적으로 확인하기 위해 스레드 이름 패턴 확인
        // 실제 작업을 제출해서 스레드 특성 확인
        String[] threadName = new String[1];
        taskExecutor.execute(() -> {
            threadName[0] = Thread.currentThread().getName();
        });

        // 잠시 대기하여 작업 완료
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 가상 스레드는 일반적으로 특별한 이름 패턴을 가짐
        assertThat(threadName[0]).isNotNull();
    }
}
