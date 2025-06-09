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
@Table(name = "testcase")
public class Testcase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer testcaseKey;
    
    @Column(name = "id", unique = true, nullable = false, length = 30)
    private String testcaseId;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "precondition", columnDefinition = "TEXT")
    private String precondition;

    @Column(name = "expected", length = 200)
    private String expected;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "mapping_key", updatable = false)
    private Integer mappingKey;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapping_key", insertable = false, updatable = false)
    private Mapping mapping;             // 매핑 (N:1)

    @OneToMany(mappedBy = "testcase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestcaseData> testcaseData = new ArrayList<>(); // 테스트케이스데이터 목록 (1:N)

    public Testcase() {}

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // getter/setter
    public Integer getTestcaseKey() {
        return testcaseKey;
    }

    public String getTestcaseId() {
        return testcaseId;
    }   

    public void setTestcaseId(String testcaseId) {
        this.testcaseId = testcaseId;
    }   

    public String getDescription() {
        return description;
    }   

    public void setDescription(String description) {
        this.description = description;
    }   

    public String getPrecondition() {
        return precondition;
    }   

    public void setPrecondition(String precondition) {
        this.precondition = precondition;
    }       

    public String getExpected() {
        return expected;
    }      

    public void setExpected(String expected) {
        this.expected = expected;
    }      

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }     

    public Integer getMappingKey() {
        return mappingKey;
    }    

    public Mapping getMapping() {
        return mapping;
    }          

   
    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
        
        if (mapping != null) {
            this.mappingKey = mapping.getMappingKey();
        }
    } 

    public List<TestcaseData> getTestcaseData() {
        return testcaseData;
    }   
}