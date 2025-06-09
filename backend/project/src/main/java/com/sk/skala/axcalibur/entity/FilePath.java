package com.sk.skala.axcalibur.entity;

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

@Entity
@Table(name = "filepath")
public class FilePath {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")   
    private Integer filePathKey;      // 파일 경로 키 (PK, AUTO_INCREMENT)
    
    @Column(name = "path", nullable = false, length = 100)
    private String path;             // 파일 경로 (NOT NULL, 최대 100자)

    @Column(name = "project_key", insertable = false, updatable = false)
    private Integer projectKey;       // 프로젝트 키 (NOT NULL)

    @Column(name = "created_at")
    private LocalDateTime createdAt;   // 생성 일자

    //연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_key", insertable = false, updatable = false)
    private Project project;         // 프로젝트 (N:1)

    //생성자
    public FilePath() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // getter/setter 메서드
    public Integer getFilePathKey() {
        return filePathKey;
    }   

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }   

    public Integer getProjectKey() {
        return projectKey;
    }     

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }   

    public Project getProject() {
        return project;
    }   

    public void setProject(Project project) {
        this.project = project;
        if (project != null) {
            this.projectKey = project.getProjectKey();
        }
    }

}