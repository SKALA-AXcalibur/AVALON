package com.sk.skala.axcalibur.spec.global.entity;

import java.util.ArrayList;
import java.util.List;

import com.sk.skala.axcalibur.spec.feature.project.entity.ApiListEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.DbDesignEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.RequestEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.ScenarioEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "project")
@ToString
public class ProjectEntity extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "`key`")
  private Integer key; // 프로젝트 키 (PK, AUTO_INCREMENT)

  @Column(name = "id", unique = true, nullable = false, length = 20)
  private String id; // 프로젝트 ID (UNIQUE)

  @OneToMany(mappedBy = "projectKey", cascade = CascadeType.REMOVE, orphanRemoval = true)
  @Builder.Default
  private List<ApiListEntity> apiLists = new ArrayList<>();

  @OneToMany(mappedBy = "projectKey", cascade = CascadeType.REMOVE, orphanRemoval = true)
  @Builder.Default
  private List<RequestEntity> requests = new ArrayList<>();

  @OneToMany(mappedBy = "projectKey", cascade = CascadeType.REMOVE, orphanRemoval = true)
  @Builder.Default
  private List<DbDesignEntity> dbDesigns = new ArrayList<>();

  @OneToMany(mappedBy = "projectKey", cascade = CascadeType.REMOVE, orphanRemoval = true)
  @Builder.Default
  private List<ScenarioEntity> scenarios = new ArrayList<>();

  @OneToMany(mappedBy = "projectKey", cascade = CascadeType.REMOVE, orphanRemoval = true)
  @Builder.Default
  private List<FilePathEntity> filePaths = new ArrayList<>();

  public ProjectEntity(String id) {
    this.id = id;
  }
}