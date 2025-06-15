package com.sk.skala.axcalibur.feature.entity;

import com.sk.skala.axcalibur.global.entity.BaseTimeEntity;

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

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "request")
public class RequestEntity extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer key;             // 요구사항 키 (PK, AUTO_INCREMENT)

    @Column(name = "id", nullable = false, length = 20, unique = true)
    private String id;               // 요구사항 아이디 

    @Column(name = "name", nullable = false, length = 50)
    private String name;             // 요구사항 이름

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;                  // 요구사항 설명

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_key", nullable = false)
    private ProjectEntity projectKey;             // 프로젝트 (N:1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_key", nullable = false)
    private PriorityEntity priorityKey;           // 우선순위 (N:1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_key", nullable = false)
    private RequestMajorEntity majorKey;   // 대분류 (N:1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "middle_key", nullable = false)
    private RequestMiddleEntity middleKey; // 중분류 (N:1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "minor_key", nullable = false)
    private RequestMinorEntity minorKey;   // 소분류 (N:1)

}