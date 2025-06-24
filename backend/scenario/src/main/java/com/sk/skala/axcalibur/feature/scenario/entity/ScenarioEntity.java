package com.sk.skala.axcalibur.feature.scenario.entity;

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
    private Integer key;          // 시나리오 키 (PK, AUTO_INCREMENT)
    
    @Column(name = "id", nullable = false, length = 30, unique = true)
    private String id;
    
    @Column(name = "name", nullable = false, length = 50)
    private String name; 
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "validation", columnDefinition = "TEXT")
    private String validation;
    
    @Column(name = "flow_chart", columnDefinition = "TEXT")
    private String flow_chart;
    
    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_key", nullable = false)
    private ProjectEntity projectKey;             // 프로젝트 키 (N:1)
    
}