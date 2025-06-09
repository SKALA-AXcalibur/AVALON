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
@Table(name = "category")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer categoryKey;         // 카테고리 키 (PK, AUTO_INCREMENT)
    
    @Column(name = "name", length = 100)
    private String name;                 // 카테고리 명 (NOT NULL, 최대 100자)
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;     // 생성 일자
    
    public Category() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // getter/setter 메서드
    public Integer getCategoryKey() {
        return categoryKey;
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