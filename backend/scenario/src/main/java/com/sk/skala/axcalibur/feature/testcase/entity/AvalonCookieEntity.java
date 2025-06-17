package com.sk.skala.axcalibur.feature.testcase.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

//  Redis 기반 아발론 쿠키 엔티티
@RedisHash(value = "avalon_cookie", timeToLive = 86400) // TTL 기간 (하루)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvalonCookieEntity {

    @Id
    private String token; //  아발론 토큰 키 (Primary Key)
    
    @Indexed
    private Integer projectKey; //  프로젝트 키
    
    private LocalDateTime createdAt; //  생성일자
    
    private LocalDateTime expiredAt; //  만료일자
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
            expiredAt = createdAt.plusDays(1);
        }
    }
}