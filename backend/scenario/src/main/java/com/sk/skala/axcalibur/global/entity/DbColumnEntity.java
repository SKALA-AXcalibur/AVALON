package com.sk.skala.axcalibur.global.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 데이터베이스 컬럼 정보 저장 테이블
 * 데이터베이스의 'db_column' 테이블과 매핑
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "db_column")
public class DbColumnEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer id; // PK, AUTO_INCREMENT   

    @Column(name = "col_name", nullable = false, length = 20)
    private String colName;         // 컬럼 명 (한글명)

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;       // 컬럼 설명

    @Column(name = "data_type", nullable = false, length = 20)
    private String dataType;         // 데이터 타입

    @Column(name = "length")
    private Integer length;          // 길이

    @Column(name = "is_pk", nullable = false)
    private Boolean isPk;            // 기본키 여부

    @Column(name = "fk", length = 30)
    private String fk;               // 외래키

    @Column(name = "is_null", nullable = false)
    private Boolean isNull;           // null 여부

    @Column(name = "const", length = 255)
    private String constraint;      // 제약조건 (자바에서 예약어이므로 const로 변경)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "db_design_key", nullable = false)
    private DbDesignEntity dbDesignKey; // 종속된 db_design Key값
    

}
