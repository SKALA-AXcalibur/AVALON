package com.sk.skala.axcalibur.feature.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.feature.entity.FilePathEntity;
import com.sk.skala.axcalibur.feature.entity.ProjectEntity;

// 6. 필요한 Repository들
public interface FilePathRepository extends JpaRepository<FilePathEntity, Integer> {
    List<FilePathEntity> findByProjectKey(ProjectEntity project);
    void deleteByProjectKey(ProjectEntity project);
}