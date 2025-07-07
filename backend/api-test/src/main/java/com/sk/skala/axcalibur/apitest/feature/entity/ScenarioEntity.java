package com.sk.skala.axcalibur.apitest.feature.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString(exclude = {"mappings"})
@Table(name = "scenario")
public class ScenarioEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "`key`", nullable = false)
  private Integer id;

  @Column(name = "id", nullable = false, unique = true)
  private String scenarioId;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @OneToMany(mappedBy = "scenario", fetch = FetchType.LAZY)
  private List<MappingEntity> mappings;

  @Column(name = "project_key", nullable = false)
  private Integer projectKey; // 프로젝트 키
}
