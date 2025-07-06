package com.sk.skala.axcalibur.spec.feature.report.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.sk.skala.axcalibur.spec.feature.project.entity.ApiListEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매핑표 항목 테이블
 * 데이터베이스의 'mapping' 테이블과 매핑
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mapping")
public class MappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer id;        // 매핑표 키(PK, AUTO_INCREMENT)
    
    @Column(name = "id", nullable = false, length = 30, unique = true)
    private String mappingId;      // 매핑표 ID(프로젝트별 유니크)

    @Column(name = "step")
    private Integer step;         // 단계명

    @Column(name = "created_at")
    private LocalDateTime createdAt;     // 생성 일자
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apilist_key", nullable = false)
    private ApiListEntity apiListKey;           // API 목록 (N:1)
    
    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_key", nullable = false)
    private ScenarioEntity scenarioKey;         // 시나리오 (N:1)
    
    @OneToMany(mappedBy = "mappingKey", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<TestCaseEntity> testCases = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
