package com.sk.skala.axcalibur.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 파라미터의 항목 유형 저장 테이블
 * 데이터베이스의 'context' 테이블과 매핑
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "context")
public class ContextEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer id;         // 항목 유형 키(PK, AUTO_INCREMENT)

    @Column(name = "name", nullable = false, unique = true, length = 10)
    private String name;        // 항목 유형 이름(body, header, query, path)
}