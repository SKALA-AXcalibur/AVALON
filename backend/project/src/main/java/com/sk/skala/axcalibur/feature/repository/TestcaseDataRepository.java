package com.sk.skala.axcalibur.feature.repository;

import java.lang.reflect.Parameter;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.feature.entity.TestcaseEntity;
import com.sk.skala.axcalibur.feature.entity.TestcaseDataEntity;

@Repository
public interface TestcaseDataRepository extends JpaRepository<TestcaseDataEntity, Integer> {
    
    // 테스트케이스별 데이터 목록 조회
    List<TestcaseDataEntity> findByTestcaseKey(TestcaseEntity testcaseKey);

    // 파라미터별 데이터 목록 조회
    List<TestcaseDataEntity> findByParameterKey(Parameter parameterKey);

    // 특정 테스트케이스의 특정 파라미터 데이터 조회
    List<TestcaseDataEntity> findByTestcaseKeyAndParameterKey(TestcaseEntity testcaseKey, Parameter parameterKey);

}
