package com.sk.skala.axcalibur.spec.feature.report.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.sk.skala.axcalibur.spec.feature.project.entity.ParameterEntity;

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

/**
 * 테스트 케이스 데이터 저장 테이블
 * 데이터베이스의 'testcase_data' 테이블과 매핑
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "testcase_data")
public class TestCaseDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer id;                     // TC data key값(PK, AUTO_INCREMENT)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "testcase_key", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TestCaseEntity testcaseKey;     // 종속된 testcase Key값

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parameter_key", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ParameterEntity parameterKey;   // parameter 정보

    @Column(name = "value", columnDefinition = "TEXT")
    private String value;                   // 데이터 값

    // TC 데이터 값 update
    public void updateValue(String value) {
        this.value = value;
    }
}