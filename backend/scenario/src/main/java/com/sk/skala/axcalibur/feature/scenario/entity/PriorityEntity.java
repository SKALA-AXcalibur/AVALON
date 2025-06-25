package com.sk.skala.axcalibur.feature.scenario.entity;


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
 * 중요도 정보 저장 테이블
 * 데이터베이스의 'priority' 테이블과 매핑
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "priority")
public class PriorityEntity extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer id;        // 중요도 키 (PK, AUTO_INCREMENT)

    @Column(name = "name", nullable = false, length = 10, unique = true)
    private String name;                 // 중요도 명 (UNIQUE)
}