package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseDataEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestcaseDataRepository extends JpaRepository<TestcaseDataEntity, Integer> {

    List<TestcaseDataEntity> findByTestcase_Id(Integer testcaseId);

    List<TestcaseDataEntity> findByTestcase_IdIn(List<Integer> testcaseIds);
}
