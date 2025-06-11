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
@Table(name = "request_major", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"})
})
public class RequestMajorEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer key;        // 대분류 키 (PK, AUTO_INCREMENT)

    @Column(name = "name", unique = true, nullable = false, length = 20)
    private String name;             // 대분류 명 (UNIQUE)
    
    @Column(name = "created_at")
    private LocalDateTime createdAt; // 생성 일자

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}