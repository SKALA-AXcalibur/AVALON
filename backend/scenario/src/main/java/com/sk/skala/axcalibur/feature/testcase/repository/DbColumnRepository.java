package com.sk.skala.axcalibur.feature.testcase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.feature.testcase.entity.DbColumnEntity;

public interface DbColumnRepository extends JpaRepository<DbColumnEntity, Integer> {
    List<DbColumnEntity> findByDbDesign_Id(Integer dbDesignId);
}