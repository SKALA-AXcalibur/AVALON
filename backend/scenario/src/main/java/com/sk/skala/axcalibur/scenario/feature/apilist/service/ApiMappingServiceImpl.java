package com.sk.skala.axcalibur.scenario.feature.apilist.service;

import com.sk.skala.axcalibur.scenario.global.code.ErrorCode;
import com.sk.skala.axcalibur.scenario.global.entity.AvalonCookieEntity;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.ApiMappingRequestDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.MappingRequestDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.MappingResponseDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.ApiDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.ScenarioDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.ApiMappingDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

import com.sk.skala.axcalibur.scenario.feature.apilist.repository.ScenarioRepository;
import com.sk.skala.axcalibur.scenario.feature.apilist.repository.ApiListRepository;
import com.sk.skala.axcalibur.scenario.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.scenario.global.repository.AvalonCookieRepository;
import com.sk.skala.axcalibur.scenario.feature.apilist.entity.ScenarioEntity;
import com.sk.skala.axcalibur.scenario.feature.apilist.entity.ApiListEntity;
import java.util.stream.Collectors;
import io.github.cdimascio.dotenv.Dotenv;

@Slf4j
@Service
public class ApiMappingServiceImpl implements ApiMappingService {

    private final String llmApiUrl;
    private final String llmApiKey;
    private final String modelName;
    
    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;
    
    @Value("${fastapi.timeout}")
    private int fastApiTimeout;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScenarioRepository scenarioRepository;
    private final ApiListRepository apiListRepository;
    private final AvalonCookieRepository avalonCookieRepository;

   public ApiMappingServiceImpl(
                               ScenarioRepository scenarioRepository,
                               ApiListRepository apiListRepository,
                               AvalonCookieRepository avalonCookieRepository) {
       this.scenarioRepository = scenarioRepository;
       this.apiListRepository = apiListRepository;
       this.avalonCookieRepository = avalonCookieRepository;

       // 여기서 .env 파일의 값을 읽어옴
       Dotenv dotenv = Dotenv.configure()
           .load();
       this.llmApiUrl = dotenv.get("LANGCHAIN_ENDPOINT");
       this.llmApiKey = dotenv.get("LANGCHAIN_API_KEY");
       this.modelName = dotenv.get("MODEL_NAME");

       // RestTemplate에 타임아웃 설정 적용
       HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
       factory.setConnectTimeout(fastApiTimeout);
       factory.setReadTimeout(fastApiTimeout);
       this.restTemplate = new RestTemplate(factory);
   }

   /**
    * 1. API 매핑 요청 (프로젝트 ID로 API/시나리오 목록 조회)
    * @param avalon 프로젝트 ID
    * @return ApiMappingRequestDto 매핑 요청 데이터
    */
   @Override
   public ApiMappingRequestDto getApiMappingList(String avalon) {
       log.info("API 매핑 요청: {}", avalon);
    
       // avalon 토큰으로 프로젝트 조회
       AvalonCookieEntity avalonCookie = avalonCookieRepository.findByToken(avalon)
            .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.PROJECT_NOT_FOUND));

        Integer projectKey = avalonCookie.getProjectKey();

        

       // 프로젝트 기준으로 시나리오 목록 조회
       List<ScenarioEntity> scenarios = scenarioRepository.findByProjectKey(projectKey);

       // 프로젝트 기준으로 API 목록 조회
       List<ApiListEntity> apis = apiListRepository.findByProjectKey(projectKey);

       // 시나리오 목록 → ScenarioDto 변환
       List<ScenarioDto> scenarioDtos = convertToScenarioDtos(scenarios);

       // API 목록 → ApiDto 변환
       List<ApiDto> apiDtos = convertToApiDtos(apis);

       // 매핑 요청 데이터 생성
       return new ApiMappingRequestDto();
   }

   /**
    * 2. API 매핑 (매핑 요청 ID로 매핑 및 결과 반환)
    * @param request 매핑 요청 데이터 (시나리오 및 API 목록)
    * @return MappingResponseDto 매핑 결과 데이터
    */
   @Override
   public MappingResponseDto doApiMapping(MappingRequestDto request) throws com.fasterxml.jackson.core.JsonProcessingException, com.fasterxml.jackson.databind.JsonMappingException {
       log.info("API 매핑 시작");

       // 요청 데이터 검증
       validationRequest(request);

       // LLM 매핑 수행
       MappingResponseDto response = llmMapping(request);

       return createResponse(response);
   }

   private void validationRequest(MappingRequestDto request) {
       if (request == null) {
           throw new IllegalArgumentException("요청 데이터가 없습니다.");
       }

       if (request.getScenarioList() == null || request.getScenarioList().isEmpty()) {
           throw new IllegalArgumentException("시나리오 목록이 없습니다.");
       }

       if (request.getApiList() == null || request.getApiList().isEmpty()) {
           throw new IllegalArgumentException("API 목록이 없습니다.");
       }

       log.info("요청 데이터 검증 완료");
   }

   private MappingResponseDto llmMapping(MappingRequestDto request) throws com.fasterxml.jackson.core.JsonProcessingException, com.fasterxml.jackson.databind.JsonMappingException {
       log.info("FastAPI 매핑 시작");
       
       // FastAPI 서버로 매핑 요청 전송
       MappingResponseDto response = callFastApiMapping(request);
       log.info("FastAPI 매핑 완료");
       return response;
   }

   /**
    * FastAPI 서버로 매핑 요청을 전송하고 결과를 받아옴
    */
   private MappingResponseDto callFastApiMapping(MappingRequestDto request) throws com.fasterxml.jackson.core.JsonProcessingException, com.fasterxml.jackson.databind.JsonMappingException {
       // FastAPI 엔드포인트 URL
       String fastApiUrl = fastApiBaseUrl + "/api/list/v1/create";
       
       // HTTP 헤더 설정
       HttpHeaders headers = new HttpHeaders();
       headers.setContentType(MediaType.APPLICATION_JSON);
       
       // 요청 데이터를 JSON으로 변환
       String requestJson = objectMapper.writeValueAsString(request);
       log.info("FastAPI 요청 데이터: {}", requestJson);
       
       // HTTP 요청 생성
       HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
       
       // FastAPI 서버로 POST 요청 전송
       ResponseEntity<String> response = restTemplate.exchange(
           fastApiUrl, 
           HttpMethod.POST, 
           entity, 
           String.class
       );
       
       log.info("FastAPI 응답 상태: {}", response.getStatusCode());
       log.info("FastAPI 응답 데이터: {}", response.getBody());
       
       // 응답을 MappingResponseDto로 파싱
       MappingResponseDto mappingResponse = objectMapper.readValue(
           response.getBody(), 
           MappingResponseDto.class
       );
       
       return mappingResponse;
   }

   private MappingResponseDto createResponse(MappingResponseDto response) {
       log.info("매핑 결과 생성");
       return response;
   }

   // ScenarioEntity → ScenarioDto 변환
   private List<ScenarioDto> convertToScenarioDtos(List<ScenarioEntity> scenarios) {
       return scenarios.stream()
           .map(scenario -> ScenarioDto.builder()
               .scenarioId(scenario.getId())
               .title(scenario.getName())
               .description(scenario.getDescription())
               .validation(scenario.getValidation())
               .build())
           .collect(Collectors.toList());
   }

   // ApiListEntity → ApiDto 변환
   private List<ApiDto> convertToApiDtos(List<ApiListEntity> apis) {
       return apis.stream()
           .map(api -> ApiDto.builder()
               .apiName(api.getName())
               .uri(api.getUri())
               .method(api.getMethod())
               .description(api.getDescription())
               .build())
           .collect(Collectors.toList());
   }

   public MappingRequestDto getMappingRequestDtoByAvalon(String avalon) {
       // avalon 토큰으로 프로젝트 조회
       AvalonCookieEntity avalonCookie = avalonCookieRepository.findByToken(avalon)
            .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.PROJECT_NOT_FOUND));

        Integer projectKey = avalonCookie.getProjectKey();

       // 프로젝트 기준으로 시나리오 목록 조회
       List<ScenarioEntity> scenarios = scenarioRepository.findByProjectKey(projectKey);

       // 프로젝트 기준으로 API 목록 조회
       List<ApiListEntity> apis = apiListRepository.findByProjectKey(projectKey);

       // 시나리오 목록 → ScenarioDto 변환
       List<ScenarioDto> scenarioDtos = convertToScenarioDtos(scenarios);

       // API 목록 → ApiDto 변환
       List<ApiDto> apiDtos = convertToApiDtos(apis);

       // 매핑 요청 데이터 생성
       return new MappingRequestDto(scenarioDtos, apiDtos);
   }
   
}