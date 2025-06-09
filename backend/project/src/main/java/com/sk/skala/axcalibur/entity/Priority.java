package com.sk.skala.axcalibur.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "priority")
public class Priority {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer priorityKey;        // 중요도 키 (PK, AUTO_INCREMENT)

    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;                 // 중요도 명 (UNIQUE)

    @Column(name = "created_at")
    private LocalDateTime createdAt;     // 생성 일자
    
    public Priority() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // getter/setter 메서드
    public Integer getPriorityKey() {
        return priorityKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}