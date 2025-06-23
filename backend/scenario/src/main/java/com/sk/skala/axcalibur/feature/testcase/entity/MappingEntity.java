package com.sk.skala.axcalibur.feature.testcase.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mapping", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id"})
})
public class MappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer id;        // 매핑표 키(PK, AUTO_INCREMENT)
    
    @Column(name = "id", nullable = false, length = 30)
    private String mappingId;      // 매핑표 ID(프로젝트별 유니크)

    @Column(name = "step")
    private Integer step;         // 단계명

    @Column(name = "created_at")
    private LocalDateTime createdAt;     // 생성 일자
    
    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_key", nullable = false)
    private ScenarioEntity scenarioKey;         // 시나리오 (N:1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apilist_key", nullable = false)
    private ApiListEntity apiListKey;           // API 목록 (N:1)

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}