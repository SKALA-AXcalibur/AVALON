package com.sk.skala.axcalibur.apitest.feature.entity;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

// Redis 기반 아발론 쿠키 엔티티
@RedisHash(value = "avalon_cookie", timeToLive = 86400) // TTL 기간 (하루)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvalonCookieEntity {

  @Id
  private String token; // 아발론 토큰 키 (Primary Key)

  @Indexed
  private Integer projectKey; // 프로젝트 키

  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();

  @Builder.Default
  private LocalDateTime expiredAt = LocalDateTime.now().plusDays(1);

  public static AvalonCookieEntity of(String token, Integer projectKey) {
    return builder()
        .token(token)
        .projectKey(projectKey)
        .build();
  }
}
