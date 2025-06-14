package com.sk.skala.axcalibur.apitest.feature.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mapping")
public class MappingEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "`key`")
  private Integer id;        // 매핑표 키(PK, AUTO_INCREMENT)

  @Column(name = "id", nullable = false, length = 30, unique = true)
  private String mappingId;           // 매핑표 ID(프로젝트별 유니크)

  @Column(name = "step")
  private Integer step;         // 단계명

  // 연관 관계
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scenario_key", nullable = false)
  private ScenarioEntity scenarioEntity;         // 시나리오 (N:1)

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "apilist_key", nullable = false)
  private ApiListEntity apiListEntity;           // API 목록 (N:1)
}
