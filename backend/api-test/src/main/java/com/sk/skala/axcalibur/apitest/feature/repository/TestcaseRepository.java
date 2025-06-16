package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestcaseRepository extends JpaRepository<TestcaseEntity, Integer> {

}
