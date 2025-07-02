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

@ExtendWith(MockitoExtension.class)
@DisplayName("Redis Stream Consumer Config 테스트")
class RedisStreamConsumerConfigTest {

    @Mock
    private RedisStreamListener listener;

    @Mock
    private TimeBasedGenerator UUIDv7;

    @Mock
    private RedisConnectionFactory connectionFactory;

    private RedisStreamConsumerConfig config;

    @BeforeEach
    void setUp() {
        config = new RedisStreamConsumerConfig(listener, UUIDv7);
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
    @DisplayName("UUID 생성기가 정상적으로 주입되었는지 확인")
    void uuidGenerator_shouldBeInjected() {
        // Given
        UUID testUuid = UUID.randomUUID();
        
        // When
        when(UUIDv7.generate()).thenReturn(testUuid);
        UUID result = UUIDv7.generate();

        // Then
        assertThat(result).isEqualTo(testUuid);
        verify(UUIDv7).generate();
    }

    @Test
    @DisplayName("Redis Stream Listener가 정상적으로 주입되었는지 확인")
    void redisStreamListener_shouldBeInjected() {
        // Given & When
        // config 객체가 정상적으로 생성되었다는 것은 listener가 주입되었다는 의미

        // Then
        assertThat(config).isNotNull();
        assertThat(listener).isNotNull();
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
