package com.sk.skala.axcalibur.feature.testcase.service;

import com.sk.skala.axcalibur.feature.testcase.dto.response.TestcaseDetailResponse;

public interface TestcaseQueryService {
    TestcaseDetailResponse getTestcaseDetail(String testcaseId);
}
