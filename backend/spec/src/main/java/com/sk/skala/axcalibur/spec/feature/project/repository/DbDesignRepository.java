package com.sk.skala.axcalibur.spec.feature.project.repository;

import com.sk.skala.axcalibur.spec.feature.project.entity.DbDesignEntity;
import com.sk.skala.axcalibur.spec.global.entity.ProjectEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbDesignRepository extends JpaRepository<DbDesignEntity, Integer> {
    
    List<DbDesignEntity> findByProjectKey(ProjectEntity project);
}
