package com.sk.skala.axcalibur.spec.feature.spec.repository;

import com.sk.skala.axcalibur.spec.feature.spec.entity.ProjectEntity;
import com.sk.skala.axcalibur.spec.feature.spec.entity.SpecFileEntity;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface SpecFileRepository extends JpaRepository<SpecFileEntity, Integer> {
    @Transactional
    void deleteAllByProject(ProjectEntity project);
}