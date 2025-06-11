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
@Table(name = "file_path")
public class FilePathEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")   
    private Integer key;      // 파일 경로 키 (PK, AUTO_INCREMENT)
    
    @Column(name = "path", nullable = false, length = 100)
    private String path;             // 파일 경로 (NOT NULL, 최대 100자)

    @Column(name = "created_at")
    private LocalDateTime createdAt;   // 생성 일자

    @Column(name = "name", nullable = false, length = 255)
    private String name;             // 파일 이름 (NOT NULL, 최대 255자)

    //연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_key", nullable = false)
    private ProjectEntity projectKey;         // 프로젝트 (N:1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_type_key", nullable = false)
    private FileTypeEntity fileTypeKey;         // 파일 타입 (N:1)

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

}