package com.sk.skala.axcalibur.spec.feature.spec.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "file_path")
public class SpecFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key")
    private Integer id; // key를 id로 변경

    @Column(nullable = false, length = 100)
    @NotBlank(message = "파일 경로는 필수입니다")
    private String path;

    @Column(name = "project_key", nullable = false)
    @NotNull(message = "프로젝트 키는 필수입니다")
    private Integer projectKey;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    // 생성자
    @Builder
    public SpecFileEntity(String path, Integer projectKey) {
        this.path = path;
        this.projectKey = projectKey;
    }

    // 필요하면 변경 메서드는 개별적으로 제공
    public void updatePath(String newPath) {
        if (newPath != null && !newPath.trim().isEmpty()) {
            this.path = newPath;
        }
    }
}