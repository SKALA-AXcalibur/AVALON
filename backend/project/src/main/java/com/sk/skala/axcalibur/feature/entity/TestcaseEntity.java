package com.sk.skala.axcalibur.feature.entity;

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
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "testcase", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id"})
})
public class TestcaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer key;
    
    @Column(name = "id", unique = true, nullable = false, length = 30)
    private String id;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "precondition", columnDefinition = "TEXT")
    private String precondition;

    @Column(name = "expected", nullable = false, length = 200)
    private String expected;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapping_key", nullable = false)
    private MappingEntity mappingKey;             // 매핑 (N:1)

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}