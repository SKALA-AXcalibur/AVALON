package com.sk.skala.axcalibur.scenario.feature.apilist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.sk.skala.axcalibur.scenario.global.entity.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "scenario")
public class ScenarioEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer key; // 시나리오 키

    @Column(name = "id", nullable = false, length = 30, unique = true)
    private String id; // 시나리오 ID

    @Column(name = "name", nullable = false, length = 100)
    private String name; // 시나리오 이름

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description; // 시나리오 설명

    @Column(name = "validation", nullable = true)
    private String validation; // 시나리오 검증 여부

    @Column(name = "flow_chart", nullable = true)
    private String flowChart; // 시나리오 흐름도

    @Column(name = "project_key", nullable = false)
    private Integer projectKey; // 프로젝트 키
    
    
}