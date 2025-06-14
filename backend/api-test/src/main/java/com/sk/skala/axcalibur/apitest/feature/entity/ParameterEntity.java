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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "parameter")
public class ParameterEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "`key`")
  private Integer id;                 // 파라미터 키 (PK, AUTO_INCREMENT)

  @Column(name = "name", length = 100)
  private String name;                // 파라미터 영문명

  @Column(name = "data_type", length = 20)
  private String dataType;            // 데이터 타입

  @Column(name = "length")
  private Integer length;             // 길이

  @Column(name = "format", length = 30)
  private String format;              // 형식

  // 연관 관계
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "apilist_key", nullable = false)
  private ApiListEntity apiListEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_key", nullable = false)
  private CategoryEntity categoryEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "context_key", nullable = false)
  private ContextEntity contextEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_key")
  private ParameterEntity parent;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
  private List<ParameterEntity> children;

}