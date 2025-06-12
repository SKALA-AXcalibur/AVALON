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

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "priority", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"})
})
public class PriorityEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer key;        // 중요도 키 (PK, AUTO_INCREMENT)

    @Column(name = "name", nullable = false, length = 50)
    private String name;                 // 중요도 명 (UNIQUE)

    @Column(name = "created_at")
    private LocalDateTime createdAt;     // 생성 일자

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}