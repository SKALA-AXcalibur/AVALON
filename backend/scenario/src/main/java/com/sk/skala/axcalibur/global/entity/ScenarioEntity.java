package com.sk.skala.axcalibur.global.entity;

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
 * 시나리오 항목 테이블
 * 데이터베이스의 'scenario' 테이블과 매핑
 */
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
    private Integer id;

    @Column(name = "id", nullable = false, unique = true)
    private String scenarioId;

    @Column(nullable = false)
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "validation", columnDefinition = "TEXT")
    private String validation;

    @Column(name = "flow_chart", columnDefinition = "TEXT")
    private String flowChart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_key", nullable = false)
    private ProjectEntity project;
}