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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "request", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "project_key"})
})
public class Request {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer requirementKey;             // 요구사항 키 (PK, AUTO_INCREMENT)

    @Column(name = "project_key", updatable = false)
    private Integer projectKey;                 // 프로젝트 키 (FK)

    @Column(name = "name", nullable = false, length = 50)
    private String name;                         // 요구사항 이름 (프로젝트별 유니크)

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;                  // 요구사항 설명

    @Column(name = "created_at")
    private LocalDateTime createdAt;             // 생성 일자

    @Column(name = "major_key")
    private Integer majorKey;                    // 대분류 키 (FK)

    @Column(name = "middle_key")
    private Integer middleKey;                   // 중분류 키 (FK)

    @Column(name = "minor_key")
    private Integer minorKey;                    // 소분류 키 (FK)

    @Column(name = "priority_key")
    private Integer priorityKey;                 // 우선순위 키 (FK)

    //연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_key", insertable = false, updatable = false)
    private Project project;                     // 프로젝트 (N:1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_key", insertable = false, updatable = false)
    private Priority priority;                   // 우선순위 (N:1)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_key", insertable = false, updatable = false)
    private RequestMajor requestMajor;           // 대분류 (N:1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "middle_key", insertable = false, updatable = false)
    private RequestMiddle requestMiddle;         // 중분류 (N:1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "minor_key", insertable = false, updatable = false)
    private RequestMinor requestMinor;           // 소분류 (N:1)

    public Request() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    // getter/setter 메서드
    public Integer getRequirementKey() {
        return requirementKey;
    }

    public Integer getProjectKey() {
        return projectKey;
    }
        

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }   

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Integer getMajorKey() {
        return majorKey;
    }

    public Integer getMiddleKey() {
        return middleKey;
    }

    public Integer getMinorKey() {
        return minorKey;
    }

    public Integer getPriorityKey() {
        return priorityKey;
    }

    public Priority getPriority() {
        return priority;
    }

    public RequestMajor getRequestMajor() {
        return requestMajor;
    }

    public RequestMiddle getRequestMiddle() {
        return requestMiddle;
    }

    public RequestMinor getRequestMinor() {
        return requestMinor;
    }

    
    public void setPriorityKey(Integer priorityKey) {
        this.priorityKey = priorityKey;
    }

    public void setMajorKey(Integer majorKey) {
        this.majorKey = majorKey;
    }

    public void setMiddleKey(Integer middleKey) {
        this.middleKey = middleKey;
    }

    public void setMinorKey(Integer minorKey) {
        this.minorKey = minorKey;
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