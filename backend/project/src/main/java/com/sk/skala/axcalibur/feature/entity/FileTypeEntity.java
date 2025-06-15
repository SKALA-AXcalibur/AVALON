package com.sk.skala.axcalibur.feature.entity;

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
 * 파일유형 정보를 담는 엔티티
 * 데이터베이스의 'file_type' 테이블과 매핑
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor       
@Builder
@Entity
@Table(name = "file_type")
public class FileTypeEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`") // 백틱키를 사용하여 key 컬럼으로 지정
    private Integer key; 

    @Column(name = "name", nullable = false, length = 20, unique = true)
    private String name; // 파일 유형 이름
    
}