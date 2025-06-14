package com.sk.skala.axcalibur.apitest.feature.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "testcase_data")
public class TestcaseDataEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "`key`", nullable = false)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "parameter_key", nullable = false)
  private ParameterEntity parameterEntity; // 파라미터 (FK, NOT NULL)

  @ManyToOne
  @JoinColumn(name = "testcase_key", nullable = false)
  private TestcaseEntity testcaseEntity; // 테스트 케이스 (FK, NOT NULL)

  @Column(name = "value", length = 50)
  private String value;

}
