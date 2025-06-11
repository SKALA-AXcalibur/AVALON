package com.sk.skala.axcalibur.feature.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "testcase_data")
public class TestcaseData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer key;

    @Column(name = "value", length = 50)
    private String value;
    
    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "testcase_key", nullable = false)
    private Testcase testcaseKey;             // 테스트케이스 (N:1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parameter_key", nullable = false)
    private Parameter parameterKey;           // 파라미터 (N:1)

}