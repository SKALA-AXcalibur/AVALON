package com.sk.skala.axcalibur.feature.testcase.entity;

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

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "category")
public class CategoryEntity extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer id;         // 파라미터 항목 키(PK, AUTO_INCREMENT)

    @Column(name = "name", nullable = false, unique = true)
    private String name;        // 파라미터 항목 이름(path/query, request, response)
}