package com.sk.skala.axcalibur.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "testcase_data")
public class TestcaseData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer testcaseDataKey;

    @Column(name = "testcase_key", updatable = false)
    private Integer testcaseKey;

    @Column(name = "parameter_key", updatable = false)
    private Integer parameterKey;

    @Column(name = "value", length = 50)
    private String value;
    
    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "testcase_key", insertable = false, updatable = false)
    private Testcase testcase;             // 테스트케이스 (N:1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parameter_key", insertable = false, updatable = false)
    private Parameter parameter;           // 파라미터 (N:1)

    public TestcaseData() {}

    // getter/setter
    public Integer getTestcaseDataKey() {
        return testcaseDataKey;
    }

    public Integer getTestcaseKey() {
        return testcaseKey;
    }

    public Integer getParameterKey() {
        return parameterKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }   

    public Testcase getTestcase() {
        return testcase;
    }
    
    public void setTestcase(Testcase testcase) {
        this.testcase = testcase;
        if (testcase != null) {
            this.testcaseKey = testcase.getTestcaseKey();
        }
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
        if (parameter != null) {
            this.parameterKey = parameter.getParameterKey();
        }
    }
}