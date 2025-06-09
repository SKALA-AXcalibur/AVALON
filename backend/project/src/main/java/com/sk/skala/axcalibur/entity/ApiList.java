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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "apilist")
public class ApiList {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer apiListKey;          // API 목록 키 (PK, AUTO_INCREMENT)

    @Column(name = "id", unique = true, nullable = false, length = 30)
    private String apiListId;            // API 목록 ID (UNIQUE)

    @Column(name = "name", length = 20)
    private String name;                 // API 목록 명 (NOT NULL, 최대 20자)

    @Column(name = "url", length = 50)
    private String url;                  // API 목록 URL (NOT NULL, 최대 50자)

    @Column(name = "path", length = 100)
    private String path;                 // API 목록 경로 (NOT NULL, 최대 100자)

    @Column(name = "method", length = 30)
    private String method;               // API 목록 메서드 (NOT NULL, 최대 30자)

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;          // API 목록 설명 (TEXT)

    @Column(name = "project_key", updatable = false)
    private Integer projectKey;         // 프로젝트 키 (NOT NULL)

    @Column(name = "created_at")
    private LocalDateTime createdAt;     // 생성 일자

    //연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_key", insertable = false, updatable = false)
    private Project project;                     // 프로젝트 (N:1)
    
    @OneToMany(mappedBy = "apiList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Parameter> parameters = new ArrayList<>();      // 파라미터 목록 (1:N)
    
    @OneToMany(mappedBy = "apiList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Mapping> mappings = new ArrayList<>();          // 매핑 목록 (1:N)

    //생성자
    public ApiList() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // getter/setter 메서드
    public Integer getApiListKey() {
        return apiListKey;
    }

    public String getApiListId() {
        return apiListId;
    }

    public void setApiListId(String apiListId) {    
        this.apiListId = apiListId;
    }

    public String getName() {
        return name;
    }   

    public void setName(String name) {
        this.name = name;
    }   

    public String getUrl() {
        return url;
    }   

    public void setUrl(String url) {
        this.url = url;
    }   

    public String getPath() {
        return path;
    }   

    public void setPath(String path) {
        this.path = path;
    }   
    
    public String getMethod() {
        return method;
    }   

    public void setMethod(String method) {
        this.method = method;
    }   

    public String getDescription() {
        return description;
    }   

    public void setDescription(String description) {
        this.description = description;
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

    public List<Parameter> getParameters() {
        return parameters;
    }       

    public List<Mapping> getMappings() {
        return mappings;
    }    
    
}