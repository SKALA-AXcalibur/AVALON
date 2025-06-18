package com.sk.skala.axcalibur.scenario.apilist.feature.entity;

import lombok.AllArgsConstructor;

import com.sk.skala.axcalibur.spec.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Integer key;

    @Column(name = "id", nullable = false, length = 30, unique = true)
    private String id;

    @Column(name = "step")
    private Integer step;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_key", nullable = false)
    private ScenarioEntity scenarioKey;
    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_list_key", nullable = false)
    private ApiListEntity apiListKey;
}
