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
@Table (name = "scenario")
public class Scenario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer scenarioKey;
    
    @Column(name = "id", unique = true, nullable = false, length = 30)
    private String scenarioId;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT", nullable = true)
    private String description;

    @Column(name = "validation", columnDefinition = "TEXT")
    private String validation;

    @Column(name = "flow_chart", columnDefinition = "TEXT")
    private String flowChart;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "project_key", updatable = false)
    private Integer projectKey;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_key", insertable = false, updatable = false)
    private Project project;             // 프로젝트 (N:1)

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Mapping> mappings = new ArrayList<>(); // 매핑 목록 (1:N)

    public Scenario() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    // getter/setter 메서드
    public Integer getScenarioKey() {
        return scenarioKey;
    }

    public String getScenarioId() {
        return scenarioId;
    }   

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
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
    
    public String getValidation() {
        return validation;
    }   

    public void setValidation(String validation) {
        this.validation = validation;
    }      

    public String getFlowChart() {
        return flowChart;
    }   

    public void setFlowChart(String flowChart) {
        this.flowChart = flowChart;
    }      

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }      

    public Integer getProjectKey() {
        return projectKey;
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

    public List<Mapping> getMappings() {
        return mappings;
    }    
}