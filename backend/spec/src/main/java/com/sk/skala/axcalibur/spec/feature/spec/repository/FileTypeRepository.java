package com.sk.skala.axcalibur.spec.feature.spec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.spec.feature.spec.entity.FileTypeEntity;

@Repository
public interface FileTypeRepository extends JpaRepository<FileTypeEntity, Integer> {

}
