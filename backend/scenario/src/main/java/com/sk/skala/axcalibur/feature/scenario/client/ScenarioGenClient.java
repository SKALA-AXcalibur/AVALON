package com.sk.skala.axcalibur.feature.scenario.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioGenRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.MappingResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioFlowResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.MappingRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioFlowRequestDto;

/**
 * ScenarioGenClient - OpenFeign 인터페이스
 * FastAPI로 시나리오 생성 요청을 전송하고 응답을 받는 클라이언트
 */
@FeignClient(name = "scenGenerator", contextId = "scenGenerator", url = "${external.fastapi.url}")
public interface ScenarioGenClient {
    
    /**
     * FastAPI로 시나리오 생성 요청 전송하고 응답 받기
     * @param requestDto 전송할 요청 데이터 (camelCase)
     * @return FastAPI 응답을 ScenarioResponseDto로 반환
     */
    @PostMapping("/api/scenario/v1/generate")
    ScenarioResponseDto sendInfoAndGetResponse(@RequestBody ScenarioGenRequestDto requestDto);

    /**
     * FastAPI로 매핑 요청 전송하고 응답 받기
     * @param requestDto 전송할 요청 데이터 (camelCase)
     * @return FastAPI 응답을 ScenarioResponseDto로 반환
     */
    @PostMapping("/api/list/v1/create")
    MappingResponseDto MappingResponse(@RequestBody MappingRequestDto requestDto);

    /**
     * FastAPI로 시나리오 흐름도 생성 요청 전송하고 응답 받기
     * @param requestDto 전송할 요청 데이터 (camelCase)
     * @return FastAPI 응답을 ScenarioFlowResponseDto로 반환
     */
    @PostMapping("/api/scenario/v1/scenario")
    ScenarioFlowResponseDto generateFlowChart(@RequestBody ScenarioFlowRequestDto requestDto);

}