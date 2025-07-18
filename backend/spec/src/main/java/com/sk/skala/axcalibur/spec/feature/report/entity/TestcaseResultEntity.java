package com.sk.skala.axcalibur.spec.feature.report.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.sk.skala.axcalibur.spec.global.entity.BaseTimeEntity;
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

/**
 * 테스트케이스 결과 항목 테이블
 * 데이터베이스의 'testcase_result' 테이블과 매핑
 */
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "testcase_key", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  // 테스트케이스 키
  private TestCaseEntity testcase;

  @Column(name = "result", columnDefinition = "TEXT", nullable = true)
  // 수행 결과
  private String result;

  @Column(name = "success", nullable = true)
  @Builder.Default
  // 성공 여부
  private Boolean success = null;

  @Column(name = "time", nullable = true)
  // 수행시간
  private Double time;

  @Column(name = "reason", length = 255, nullable = true)
  // 성공, 실패 특이사항
  private String reason;

}