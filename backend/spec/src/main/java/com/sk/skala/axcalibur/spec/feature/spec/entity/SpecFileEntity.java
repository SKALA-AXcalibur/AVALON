package com.sk.skala.axcalibur.spec.feature.spec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

import com.sk.skala.axcalibur.spec.global.entity.BaseTimeEntity;

/**
 * 명세서 파일의 메타데이터 정보를 담는 엔티티
 * 데이터베이스의 'file_path' 테이블과 매핑
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor       
@Builder
@Entity
@Table(name = "file_path", uniqueConstraints = @UniqueConstraint(columnNames = {"project_key", "file_type_key"}))
public class SpecFileEntity extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key")
    private Integer id; // key를 id로 변경

    @Column(nullable = false, length = 100)
    private String path;

    @Column(nullable = false, length = 255)
    private String name; // 파일 이름 추가

    @Column(name = "file_type_key", nullable = false)
    private Integer fileTypeKey; // 파일 타입 키 추가

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_key", nullable = false) // project_key 컬럼으로 지정
    private ProjectEntity project; // 프로젝트 엔티티와 연관 관계 설정

}