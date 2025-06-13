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
@Table(name = "file_path")
public class FilePathEntity extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer key;
    
    @Column(name = "path", nullable = false, unique = true, length = 100)
    private String path;
    
    @Column(name = "file_type_key", nullable = false)
    private Integer fileTypeKey;  // file_type 테이블 참조
    
    @Column(name = "name", length = 255, nullable = false)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_key", nullable = false)
    private ProjectEntity projectKey;
}