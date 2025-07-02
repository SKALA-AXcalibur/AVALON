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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "api_list")
public class ApiListEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "`key`")
  private Integer id;          // API 목록 키 (PK, AUTO_INCREMENT)

  @Column(name = "url", nullable = false, length = 50)
  private String url;                  // API 목록 URL (NOT NULL, 최대 50자)

  @Column(name = "path", nullable = false, length = 100)
  private String path;                 // API 목록 경로 (NOT NULL, 최대 100자)

  @Column(name = "method", nullable = false, length = 30)
  private String method;               // API 목록 메서드 (NOT NULL, 최대 30자)

  @OneToMany(mappedBy = "apiList", fetch = FetchType.LAZY)
  private List<ParameterEntity> parameters; // API 목록에 속한 파라미터 목록

}
