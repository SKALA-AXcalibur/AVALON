package com.sk.skala.axcalibur.spec.feature.spec.enums;

/**
 * 명세서 파일의 유형을 나타낸다.
 * 각 항목은 file_type 테이블의 name 컬럼과 매핑되며, 
 * 파일 경로 조회 및 분석 처리에서 사용됩니다.
 *
 * requirement: 요구사항 명세서
 * interface_def: 인터페이스 정의서
 * interface_design: 인터페이스 설계서
 * database_design: DB 설계서
 */
public enum FileTypeName {
    REQUIREMENT_FILE,
    INTERFACE_DEFINITION,
    INTERFACE_DESIGN,
    DATABASE_DESIGN;
}