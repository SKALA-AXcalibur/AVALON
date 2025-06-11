package com.sk.skala.axcalibur.feature.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "file_type", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"})
})
public class FileTypeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")   
    private Integer key;      // 파일 경로 키 (PK, AUTO_INCREMENT)

    @Column(name = "created_at")
    private LocalDateTime createdAt;   // 생성 일자

    @Column(name = "name", nullable = false, length = 20)
    private String name;             // 파일유형 이름 (NOT NULL, 최대 20자)

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

}