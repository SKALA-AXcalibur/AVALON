package com.sk.skala.axcalibur.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    // 프로젝트 ID로 조회 (비즈니스 키)
    Optional<Project> findByProjectId(String projectId);

    // 프로젝트 ID 존재 여부
    boolean existsByProjectId(String projectId);

    // avalon 값으로 프로젝트 조회
    Optional<Project> findByAvalon(String avalon);
}
