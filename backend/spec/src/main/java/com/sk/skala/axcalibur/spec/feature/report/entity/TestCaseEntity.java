package com.sk.skala.axcalibur.spec.feature.report.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.sk.skala.axcalibur.spec.global.entity.BaseTimeEntity;

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
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 테스트 케이스 저장 테이블
 * 데이터베이스의 'testcase' 테이블과 매핑
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "testcase")
public class TestCaseEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer id;         // 테스트케이스 ID(PK, AUTO_INCREMENT)

    @Column(name = "id", nullable = false, length = 50, unique = true)
    private String testcaseId;           // 테스트케이스 ID

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;  // 설명

    @Column(name = "precondition", columnDefinition = "TEXT")
    private String precondition; // 사전조건

    @Column(name = "expected", nullable = false, length = 200)
    private String expected;     // 예상 결과(줄글로 생성)

    @Column(name = "status")
    private Integer status;     // 예상 상태 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapping_key", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MappingEntity mappingKey;

    // TC 내용 update
    public void update(String precondition, String description, String expectedResult, Integer status) {
        this.precondition = precondition;
        this.description = description;
        this.expected = expectedResult;
        this.status = status;
    }
}