package com.sk.skala.axcalibur.feature.testcase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.feature.testcase.entity.DbDesignEntity;

public interface DbDesignRepository extends JpaRepository<DbDesignEntity, Integer> {
    List<DbDesignEntity> findByProject_Id(Integer projectId);
}
