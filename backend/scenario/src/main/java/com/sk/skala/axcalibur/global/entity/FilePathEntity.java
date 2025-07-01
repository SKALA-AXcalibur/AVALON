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
 * 파일 경로 정보 저장 테이블
 * 데이터베이스의 'file_path' 테이블과 매핑
 */
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
    private Integer id; // PK, AUTO_INCREMENT
    
    @Column(name = "path", nullable = false,  length = 100)
    private String path; // 파일 경로

    @Column(name = "name", length = 255, nullable = false)
    private String name; // 파일 명
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_type_key", nullable = false)
    private FileTypeEntity fileTypeKey;  // file_type 테이블 참조
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_key", nullable = false)
    private ProjectEntity projectKey; // 프로젝트 키

    // 같은 유형의 파일이 있으면 이름과 경로를 업데이트
    public void updateFileInfo(String path, String name) {
        this.path = path;
        this.name = name;
    }
}