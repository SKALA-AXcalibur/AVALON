package com.sk.skala.axcalibur.apitest.feature.entity;

import com.sk.skala.axcalibur.apitest.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "testcase_result")
public class TestcaseResultEntity extends BaseTimeEntity {

  @Id
  @Column(name = "`key`", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  // 테스트케이스 결과 키
  private Integer id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "testcase_id", nullable = false)
  // 테스트케이스 키
  private TestcaseEntity testcaseEntity;

  @Column(name = "result", length = 50)
  // 수행 결과
  private String result;

  @Column(name = "success", nullable = false)
  @Builder.Default
  // 성공 여부
  private Boolean success = false;

  @Column(name = "time")
  // 수행시간
  private LocalDateTime time;


}