package com.sk.skala.axcalibur.feature.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "project")
public class ProjectEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "`key`")
   private Integer key;               // 프로젝트 키 (PK, AUTO_INCREMENT)

   @Column(name = "id", unique = true, nullable = false, length = 20)
   private String id;                // 프로젝트 ID (UNIQUE)

   @Column(name = "created_at", updatable = false)
   private LocalDateTime createdAt;        // 생성 일자

    public ProjectEntity(String id) {
        this.id = id;
    }

   @PrePersist
   protected void onCreate() {
    if (createdAt == null) {
        createdAt = LocalDateTime.now();
    }

    
}
}