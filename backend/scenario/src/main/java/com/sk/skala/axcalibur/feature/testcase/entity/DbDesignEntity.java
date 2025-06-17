package com.sk.skala.axcalibur.feature.testcase.entity;

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

import com.sk.skala.axcalibur.global.entity.BaseTimeEntity;

/**
 * 테이블 설계서 파싱 결과 저장 테이블
 * 데이터베이스의 'db_design' 테이블과 매핑
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "db_design")
public class DbDesignEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer id;         // 테이블 설계서 키(PK, AUTO INCREMENT)

    @Column(name = "name", nullable = false)
    private String name;        // 테이블 명

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_key", nullable = false)
    private ProjectEntity project;  // 프로젝트 (N:1)
}
