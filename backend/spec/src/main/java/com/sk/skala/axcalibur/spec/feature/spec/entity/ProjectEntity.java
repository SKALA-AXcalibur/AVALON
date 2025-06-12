package com.sk.skala.axcalibur.spec.feature.spec.entity;

import com.sk.skala.axcalibur.spec.global.entity.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * 프로젝트 정보를 담는 엔티티
 * 데이터베이스의 'project' 테이블과 매핑
 */
@Getter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "project")
public class ProjectEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`") // 백틱키를 사용하여 key 컬럼으로 지정
    private Integer id; // key를 id로 변경

    @Column(name = "id", nullable = false, length = 20, unique = true)
    private String projectId; // 프로젝트 ID

}
