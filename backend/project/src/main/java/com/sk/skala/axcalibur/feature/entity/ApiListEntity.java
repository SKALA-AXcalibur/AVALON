package com.sk.skala.axcalibur.feature.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "api_list", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"project_key", "id"})
})
public class ApiListEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer key;          // API 목록 키 (PK, AUTO_INCREMENT)

    @Column(name = "id", nullable = false, length = 30)
    private String id;            // API 목록 ID (프로젝트별 유니크)

    @Column(name = "name", nullable = false, length = 20)
    private String name;                 // API 목록 명 (NOT NULL, 최대 20자)

    @Column(name = "url", nullable = false, length = 50)
    private String url;                  // API 목록 URL (NOT NULL, 최대 50자)

    @Column(name = "path", nullable = false, length = 100)
    private String path;                 // API 목록 경로 (NOT NULL, 최대 100자)

    @Column(name = "method", nullable = false, length = 30)
    private String method;               // API 목록 메서드 (NOT NULL, 최대 30자)

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;          // API 목록 설명 (TEXT)

    @Column(name = "created_at")
    private LocalDateTime createdAt;     // 생성 일자

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_key", nullable = false)
    private ProjectEntity projectKey;             // 프로젝트 (N:1)

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}