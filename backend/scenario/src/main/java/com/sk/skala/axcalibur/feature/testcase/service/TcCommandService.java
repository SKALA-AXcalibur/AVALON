package com.sk.skala.axcalibur.feature.testcase.service;

import com.sk.skala.axcalibur.feature.testcase.dto.request.TcUpdateRequest;

public interface TcCommandService {
    void deleteTestcase(String tcId, Integer projectId);
    void updateTestcase(String tcId, Integer projectId, TcUpdateRequest request);
}
