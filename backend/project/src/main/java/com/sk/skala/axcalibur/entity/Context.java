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
@Table(name = "context")
public class Context {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer contextKey;         // 컨텍스트 키 (PK, AUTO_INCREMENT)
    
    @Column(name = "name", length = 100)
    private String name;                 // 컨텍스트 명 (NOT NULL, 최대 100자)

    @Column(name = "created_at")
    private LocalDateTime createdAt;     // 생성 일자
    
    public Context() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // getter/setter 메서드
    public Integer getContextKey() {
        return contextKey;
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