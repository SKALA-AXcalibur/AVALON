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
import lombok.Setter;

/**
 * 파라미터 저장 테이블
 * 데이터베이스의 'parameter' 테이블과 매핑
 * 부모키 조회 -> 저장 위해 Setter 정의
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "parameter")
public class ParameterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer id;                 // 파라미터 키 (PK, AUTO_INCREMENT)

    @Column(name = "name_ko", length = 100)
    private String nameKo;              // 파라미터 한글명

    @Column(name = "name", length = 100)
    private String name;                // 파라미터 영문명

    @Column(name = "data_type", length = 20)
    private String dataType;            // 데이터 타입

    @Column(name = "length")
    private Integer length;             // 길이

    @Column(name = "format", length = 30)
    private String format;              // 형식

    @Column(name = "default_value", length = 255)
    private String defaultValue;        // 기본값

    @Column(name = "required")
    private Boolean required;           // 필수 여부

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;         // 설명
    
    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apilist_key", nullable = false)
    private ApiListEntity apiListKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_key", nullable = false)
    private CategoryEntity categoryKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_key", nullable = false)
    private ContextEntity contextKey;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_key")
    private ParameterEntity parentKey;
}
