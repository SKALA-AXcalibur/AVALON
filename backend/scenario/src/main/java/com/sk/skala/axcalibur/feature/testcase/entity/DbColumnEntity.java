package com.sk.skala.axcalibur.feature.testcase.entity;

import com.sk.skala.axcalibur.global.entity.BaseTimeEntity;

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
 * 테이블 컬럼 저장 테이블
 * 데이터베이스의 'db_column' 테이블과 매핑
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "db_column")
public class DbColumnEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer id;                 // 테이블 컬럼 키(PK, AUTO INCREMENT)

    @Column(name = "name", nullable = false)
    private String name;                // 컬럼명(한글)

    @Column(name = "description")
    private String description;         // 컬럼 설명

    @Column(name = "type", nullable = false)
    private String type;                // 데이터 타입

    @Column(name = "length")
    private Integer length;             // 데이터 길이

    @Column(name = "is_pk", nullable = false)
    private boolean isPk;               // PK여부

    @Column(name = "is_null", nullable = false)
    private boolean isNull;             // NULL 여부

    @Column(name = "fk")
    private String fk;                  // 외래키 정보

    @Column(name = "constraint")
    private String constraint;          // 제약조건

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "db_design_key", nullable = false)
    private DbDesignEntity dbDesign;
}