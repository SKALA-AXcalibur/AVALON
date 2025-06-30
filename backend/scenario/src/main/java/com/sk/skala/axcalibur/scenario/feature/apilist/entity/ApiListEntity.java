package com.sk.skala.axcalibur.scenario.feature.apilist.entity;

import com.sk.skala.axcalibur.scenario.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "api_list")
public class ApiListEntity extends BaseTimeEntity {
     
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer key;          // API 목록 키 (PK, AUTO_INCREMENT)

    @Column(name = "id", nullable = false, length = 30, unique = true)
    private String id;            // API 목록 ID (프로젝트별 유니크)

    @Column(name = "name", nullable = false, length = 20)
    private String name;                 // API 목록 명 (NOT NULL, 최대 20자)

    @Column(name = "uri", nullable = false, length = 50)
    private String uri;                  // API 목록 URL (NOT NULL, 최대 50자)

    @Column(name = "path", nullable = false, length = 100)
    private String path;                 // API 목록 경로 (NOT NULL, 최대 100자)

    @Column(name = "method", nullable = false, length = 30)
    private String method;               // API 목록 메서드 (NOT NULL, 최대 30자)

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;          // API 목록 설명 (TEXT)

    @Column(name = "project_key", nullable = false)
    private Integer projectKey;             // 프로젝트 키

    @Column(name = "request_key", nullable = false)
    private Integer requestKey;             // 요청 키
}