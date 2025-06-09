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
@Table(name = "mapping")
public class Mapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer mappingKey;
    
    @Column(name = "id", unique = true, nullable = false, length = 30)
    private String mappingId;

    @Column(name = "step")
    private Integer step;

    @Column(name = "scenario_key", updatable = false)
    private Integer scenarioKey;

    @Column(name = "apilist_key", updatable = false)
    private Integer apiListKey;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_key", insertable = false, updatable = false)
    private Scenario scenario;         // 시나리오 (N:1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apilist_key", insertable = false, updatable = false)
    private ApiList apiList;           // API 목록 (N:1)

    @OneToMany(mappedBy = "mapping", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Testcase> mappingDetails = new ArrayList<>(); // 테스트케이스 목록 (1:N)

    public Mapping() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    // getter/setter 메서드
    public Integer getMappingKey() {
        return mappingKey;
    }

    public String getMappingId() {
        return mappingId;
    }

    public void setMappingId(String mappingId) {
        this.mappingId = mappingId;
    }
    
    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Integer getScenarioKey() {
        return scenarioKey;
    }

    public Integer getApiListKey() {
        return apiListKey;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Scenario getScenario() {
        return scenario;
    }
    
    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
        if (scenario != null) {
            this.scenarioKey = scenario.getScenarioKey();
        }
    }

    public ApiList getApiList() {
        return apiList;
    }

    public void setApiList(ApiList apiList) {
        this.apiList = apiList;
        if (apiList != null) {
            this.apiListKey = apiList.getApiListKey();
        }
    }

}