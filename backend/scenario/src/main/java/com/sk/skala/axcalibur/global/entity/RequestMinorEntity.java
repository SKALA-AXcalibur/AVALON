package com.sk.skala.axcalibur.global.entity;

import com.sk.skala.axcalibur.global.entity.BaseTimeEntity;

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
 * 요구사항 소분류 정보 저장 테이블
 * 데이터베이스의 'request_minor' 테이블과 매핑
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "request_minor")
public class RequestMinorEntity extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")   
    private Integer id;             // 소분류 키 (PK, AUTO_INCREMENT)

    @Column(name = "name", nullable = false, length = 20)
    private String name;                 // 소분류 명
}
