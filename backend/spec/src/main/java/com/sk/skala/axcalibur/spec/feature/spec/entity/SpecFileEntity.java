package com.sk.skala.axcalibur.spec.feature.spec.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
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
    private String path;

    @Column(name = "project_key", nullable = false)
    private Integer projectKey;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false, length = 255)
    private String name; // 파일 이름 추가

    @Column(name = "file_type_key", nullable = false)
    private Integer fileTypeKey; // 파일 타입 키 추가

    // 생성자
    @Builder
    public SpecFileEntity(String path, Integer projectKey, String name, Integer fileTypeKey) {
        this.path = path;
        this.projectKey = projectKey;
        this.name = name;
        this.fileTypeKey = fileTypeKey;
    }

}