package com.sk.skala.axcalibur.apitest.feature.code;


public enum ApiTestStatus {
  CREATED("CREATED", "테스트 생성됨"),
  PENDING("PENDING", "테스트 대기 중"),
  RUNNING("RUNNING", "테스트 실행 중"),
  SUCCESS("SUCCESS", "테스트 성공"),
  FAILED("FAILED", "테스트 실패"),
  CANCELLED("CANCELLED", "테스트 취소됨"),
  ERROR("ERROR", "테스트 오류 발생"),
  TIMEOUT("TIMEOUT", "테스트 시간 초과");

  private final String code;
  private final String desc;

  ApiTestStatus(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }
}
