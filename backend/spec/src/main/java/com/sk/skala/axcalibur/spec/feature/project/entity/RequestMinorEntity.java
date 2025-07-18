package com.sk.skala.axcalibur.spec.feature.project.entity;

import com.sk.skala.axcalibur.spec.global.entity.BaseTimeEntity;

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
    private Integer key;             // 소분류 키 (PK, AUTO_INCREMENT)

    @Column(name = "name", nullable = false, length = 20)
    private String name;                 // 소분류 명
}
