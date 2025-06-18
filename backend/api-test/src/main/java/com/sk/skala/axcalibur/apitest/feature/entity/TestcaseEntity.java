package com.sk.skala.axcalibur.apitest.feature.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "testcase")
public class TestcaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "`key`", nullable = false)
  private Integer id;

  @Column(name = "id", nullable = false, unique = true, length = 20)
  private String testcaseId;

  @Column(name = "description", nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "precondition", columnDefinition = "TEXT")
  private String precondition;

  @Column(name = "expected", nullable = false, length = 200)
  private String expected;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mapping_key", nullable = false)
  private MappingEntity mapping;


  @OneToMany(mappedBy = "testcase")
  private List<TestcaseDataEntity> testcaseDatas; // 테스트 케이스에 속한 데이터 목록

  @OneToMany(mappedBy = "testcase")
  private List<TestcaseResultEntity> testcaseResults; // 테스트 케이스에 속한 결과

}
