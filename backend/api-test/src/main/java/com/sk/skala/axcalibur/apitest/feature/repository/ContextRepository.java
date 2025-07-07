package com.sk.skala.axcalibur.apitest.feature.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.apitest.feature.entity.ContextEntity;

@Repository
public interface ContextRepository extends JpaRepository<ContextEntity, Integer> {

}
