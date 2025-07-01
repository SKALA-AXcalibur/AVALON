package com.sk.skala.axcalibur.scenario.feature.apilist.entity;

import lombok.AllArgsConstructor;

import com.sk.skala.axcalibur.scenario.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mapping")
public class MappingEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer key; // 매핑 키

    @Column(name = "id", nullable = false, length = 30, unique = true)
    private String id; // 매핑 ID

    @Column(name = "step")
    private Integer step; // 단계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_key", nullable = false)
    private ScenarioEntity scenarioKey; // 시나리오 키
    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_list_key", nullable = false)
    private ApiListEntity apiListKey; // API 목록 키
}
