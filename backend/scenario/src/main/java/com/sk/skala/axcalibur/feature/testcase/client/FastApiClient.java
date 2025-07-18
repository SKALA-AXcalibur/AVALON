package com.sk.skala.axcalibur.feature.testcase.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sk.skala.axcalibur.feature.testcase.dto.request.TcGenerationRequest;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcGenerationResponse;

/**
 * testcase의 FastAPI 호출부
 */
@FeignClient(name = "tcGenerator", contextId = "tcGenerator", url = "${external.fastapi.url}")
public interface FastApiClient {
        @PostMapping("/api/tc/v1/{scenarioId}")
        TcGenerationResponse generate(@PathVariable String scenarioId,
                                        @RequestBody TcGenerationRequest payload);
}
