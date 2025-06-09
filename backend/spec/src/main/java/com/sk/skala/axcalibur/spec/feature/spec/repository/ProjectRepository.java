package com.sk.skala.axcalibur.spec.feature.spec.repository;

import com.sk.skala.axcalibur.spec.feature.spec.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Integer> {
    
    Optional<ProjectEntity> findByProjectId(String projectId);

}