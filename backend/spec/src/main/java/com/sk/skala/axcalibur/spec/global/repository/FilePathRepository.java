package com.sk.skala.axcalibur.spec.global.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.spec.global.entity.FilePathEntity;
import com.sk.skala.axcalibur.spec.global.entity.ProjectEntity;

// 6. 필요한 Repository들
public interface FilePathRepository extends JpaRepository<FilePathEntity, Integer> {
    List<FilePathEntity> findByProjectKey(ProjectEntity project);
    void deleteByProjectKey(ProjectEntity project);
}