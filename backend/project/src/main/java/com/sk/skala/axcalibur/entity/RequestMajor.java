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
@Table(name = "requestmajor")
public class RequestMajor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer majorKey;        // 대분류 키 (PK, AUTO_INCREMENT)

    @Column(name = "name", unique = true, nullable = false, length = 20)
    private String name;             // 대분류 명 (UNIQUE)
    
    @Column(name = "created_at")
    private LocalDateTime createdAt; // 생성 일자

    public RequestMajor() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    // getter/setter 메서드
    public Integer getMajorKey() {
        return majorKey;
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