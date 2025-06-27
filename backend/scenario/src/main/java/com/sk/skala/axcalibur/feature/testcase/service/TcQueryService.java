package com.sk.skala.axcalibur.feature.testcase.service;

import com.sk.skala.axcalibur.feature.testcase.dto.response.TcDetailResponse;

public interface TcQueryService {
    TcDetailResponse getTestcaseDetail(String testcaseId);
}
