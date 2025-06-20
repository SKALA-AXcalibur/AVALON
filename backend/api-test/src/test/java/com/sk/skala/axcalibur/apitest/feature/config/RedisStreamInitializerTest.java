package com.sk.skala.axcalibur.apitest.feature.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.sk.skala.axcalibur.apitest.feature.code.StreamConstants;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;

@ExtendWith(MockitoExtension.class)
class RedisStreamInitializerTest {

    @Mock
    private RedisTemplate<String, ApiTaskDto> redisTemplate;

    @Mock
    private StreamOperations<String, Object, Object> streamOperations;

    private RedisStreamInitializer redisStreamInitializer;

    @BeforeEach
    void setUp() {
        redisStreamInitializer = new RedisStreamInitializer(redisTemplate);
        when(redisTemplate.opsForStream()).thenReturn(streamOperations);
    }

    @Test
    void initializeStreamAndGroup_shouldCreateGroup_whenSuccessful() {
        // Given
        when(streamOperations.createGroup(eq(StreamConstants.STREAM_KEY), eq(StreamConstants.GROUP_NAME)))
            .thenReturn("OK");

        // When
        redisStreamInitializer.initializeStreamAndGroup();

        // Then
        verify(streamOperations).createGroup(StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME);
    }

    @Test
    void initializeStreamAndGroup_shouldHandleBusyGroupException() {
        // Given
        RedisSystemException exception = mock(RedisSystemException.class);
        Throwable cause = mock(Throwable.class);

        when(cause.getMessage()).thenReturn("BUSYGROUP Consumer Group name already exists");
        when(exception.getCause()).thenReturn(cause);

        doThrow(exception).when(streamOperations).createGroup(any(), any());

        // When
        redisStreamInitializer.initializeStreamAndGroup();

        // Then
        verify(streamOperations).createGroup(StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME);
    }

    @Test
    void initializeStreamAndGroup_shouldHandleOtherRedisSystemException() {
        // Given
        RedisSystemException exception = mock(RedisSystemException.class);
        Throwable cause = mock(Throwable.class);

        when(cause.getMessage()).thenReturn("Some other Redis error");
        when(exception.getCause()).thenReturn(cause);

        doThrow(exception).when(streamOperations).createGroup(any(), any());

        // When
        redisStreamInitializer.initializeStreamAndGroup();

        // Then
        verify(streamOperations).createGroup(StreamConstants.STREAM_KEY, StreamConstants.GROUP_NAME);
    }
}
