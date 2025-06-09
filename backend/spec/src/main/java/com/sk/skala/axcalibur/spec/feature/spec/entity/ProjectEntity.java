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
@Table(name = "project")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`") // 백틱키를 사용하여 key 컬럼으로 지정
    private Integer id; // key를 id로 변경

    @Column(nullable = false, length = 20, unique = true)
    private String projectId; // 프로젝트 ID

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public ProjectEntity(String projectId) {
        this.projectId = projectId;
    }

}
