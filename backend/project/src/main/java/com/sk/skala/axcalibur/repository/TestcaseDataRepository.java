package com.sk.skala.axcalibur.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.entity.TestcaseData;

@Repository
public interface TestcaseDataRepository extends JpaRepository<TestcaseData, Integer> {
    
    // 테스트케이스별 데이터 목록 조회
    List<TestcaseData> findByTestcaseKey(Integer testcaseKey);

    // 파라미터별 데이터 목록 조회
    List<TestcaseData> findByParameterKey(Integer parameterKey);

    // 특정 테스트케이스의 특정 파라미터 데이터 조회
    List<TestcaseData> findByTestcaseKeyAndParameterKey(Integer testcaseKey, Integer parameterKey);

}
