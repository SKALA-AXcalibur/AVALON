package com.sk.skala.axcalibur.feature.scenario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.feature.scenario.entity.FileTypeEntity;
import com.sk.skala.axcalibur.feature.scenario.entity.FilePathEntity;
import com.sk.skala.axcalibur.feature.scenario.entity.ProjectEntity;

// 6. 필요한 Repository들
public interface FilePathRepository extends JpaRepository<FilePathEntity, Integer> {
    List<FilePathEntity> findByProjectKey(ProjectEntity project);
    Optional<FilePathEntity> findByProjectKeyAndFileTypeKey(ProjectEntity projectKey, FileTypeEntity fileTypeKey);
    void deleteByProjectKey(ProjectEntity project);
}
