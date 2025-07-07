package com.sk.skala.axcalibur.apitest.feature.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "context")
@Schema(description = "body, header, query, path 항목만 존재하는 코드값 저장 테이블")
public class ContextEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "`key`")
  private Integer key; // 컨텍스트 키 (PK, AUTO_INCREMENT)

  @Column(name = "name", nullable = false, length = 10)
  private String name; // 컨텍스트 명 (NOT NULL)

}