package com.sk.skala.axcalibur.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;                           

@Entity
@Table(name = "project")
public class Project {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "`key`")
   private Integer projectKey;               // 프로젝트 키 (PK, AUTO_INCREMENT)

   @Column(name = "project_id", unique = true, nullable = false, length = 20)
   private String projectId;                // 프로젝트 ID (UNIQUE)

   @Column(name = "created_at")
   private LocalDateTime createdAt;        // 생성 일자

   //연관 관계
   @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   private List<Request> requirements = new ArrayList<>(); // 요구사항 목록 (1:N)

   @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   private List<FilePath> filePaths = new ArrayList<>(); // 파일 경로 목록 (1:N)

   @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   private List<ApiList> apiLists = new ArrayList<>(); // API 목록 (1:N)

   @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   private List<Scenario> scenarios = new ArrayList<>(); // 시나리오 목록 (1:N)

   public Project() {}

   @PrePersist
   protected void onCreate() {
    if (createdAt == null) {
        createdAt = LocalDateTime.now();
    }
}

   // getter/setter 메서드
   public Integer getProjectKey() {
    return projectKey;
   }

   public String getProjectId() {
    return projectId;
   }

   public void setProjectId(String projectId) {
    this.projectId = projectId;
   }

   public LocalDateTime getCreatedAt() {
    return createdAt;
   }

   public List<Request> getRequirements() {
    return requirements;
   }

   public List<FilePath> getFilePaths() {
    return filePaths;
   }

   public List<ApiList> getApiLists() {
    return apiLists;
   }

   public List<Scenario> getScenarios() {
    return scenarios;
   }      
   
}