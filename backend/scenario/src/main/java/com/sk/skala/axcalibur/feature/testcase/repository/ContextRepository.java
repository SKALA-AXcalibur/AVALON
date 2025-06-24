package com.sk.skala.axcalibur.feature.testcase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.global.entity.ContextEntity;

public interface ContextRepository extends JpaRepository<ContextEntity, Integer> {
    Optional<ContextEntity> findByName(String name);
}
