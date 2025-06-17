package com.sk.skala.axcalibur.spec.global.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.spec.global.entity.FileTypeEntity;

public interface FileTypeRepository extends JpaRepository<FileTypeEntity, Integer> {
    Optional<FileTypeEntity> findByName(String name);
}