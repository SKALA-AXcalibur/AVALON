package com.sk.skala.axcalibur.scenario.feature.apilist.service;

import com.sk.skala.axcalibur.scenario.global.code.ErrorCode;
import com.sk.skala.axcalibur.scenario.global.entity.AvalonCookieEntity;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.ApiMappingRequestDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.MappingRequestDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.MappingResponseDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.ApiDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.ScenarioDto;
import com.sk.skala.axcalibur.scenario.feature.apilist.dto.ApiMappingDto;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import com.sk.skala.axcalibur.scenario.feature.apilist.repository.ScenarioRepository;
import com.sk.skala.axcalibur.scenario.feature.apilist.repository.ApiListRepository;
import com.sk.skala.axcalibur.scenario.feature.apilist.repository.MappingRepository;
import com.sk.skala.axcalibur.scenario.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.scenario.global.repository.AvalonCookieRepository;
import com.sk.skala.axcalibur.scenario.feature.apilist.entity.ScenarioEntity;
import com.sk.skala.axcalibur.scenario.feature.apilist.entity.ApiListEntity;
import com.sk.skala.axcalibur.scenario.feature.apilist.entity.MappingEntity;

import jakarta.annotation.PostConstruct;

@Slf4j
@Service
public class ApiMappingServiceImpl implements ApiMappingService {
    
    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;
    
    @Value("${rest.template.connect-timeout}")
    private int connectTimeout;
    
    @Value("${rest.template.read-timeout}")
    private int readTimeout;
    
    private RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScenarioRepository scenarioRepository;
    private final ApiListRepository apiListRepository;
    private final MappingRepository mappingRepository;
    private final AvalonCookieRepository avalonCookieRepository;

   public ApiMappingServiceImpl(
                               ScenarioRepository scenarioRepository,
                               ApiListRepository apiListRepository,
                               MappingRepository mappingRepository,
                               AvalonCookieRepository avalonCookieRepository) {
       this.scenarioRepository = scenarioRepository;
       this.apiListRepository = apiListRepository;
       this.mappingRepository = mappingRepository;
       this.avalonCookieRepository = avalonCookieRepository;
   }

   @PostConstruct
   public void init() {
       // RestTemplate에 타임아웃 설정 적용 (설정 파일에서 읽어옴)
       SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
       factory.setConnectTimeout(connectTimeout); // 설정 파일에서 읽어온 값 사용
       factory.setReadTimeout(readTimeout);       // 설정 파일에서 읽어온 값 사용
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
       
       // 매핑 요청 데이터 생성 (처리 시간만 반환)
       return new ApiMappingRequestDto();
   }

   /**
    * 2. API 매핑 (매핑 요청 ID로 매핑 및 결과 반환)
    * @param request 매핑 요청 데이터 (시나리오 및 API 목록)
    * @param avalon 프로젝트 ID
    * @return MappingResponseDto 매핑 결과 데이터
    */
   @Override
   public MappingResponseDto doApiMapping(MappingRequestDto request, String avalon) throws com.fasterxml.jackson.core.JsonProcessingException {
       log.info("API 매핑 시작");

       // 요청 데이터 검증
       validationRequest(request);

       // LLM 매핑 수행
       MappingResponseDto response = llmMapping(request, avalon);

       // 매핑 결과를 DB에 저장
       saveMappingResult(response);

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

   private MappingResponseDto llmMapping(MappingRequestDto request, String avalon) throws com.fasterxml.jackson.core.JsonProcessingException {
       log.info("FastAPI 매핑 시작");
       
       // FastAPI 서버로 매핑 요청 전송
       MappingResponseDto response = callFastApiMapping(request, avalon);
       log.info("FastAPI 매핑 완료");
       return response;
   }

   /**
    * FastAPI 서버로 매핑 요청을 전송하고 결과를 받아옴
    */
   private MappingResponseDto callFastApiMapping(MappingRequestDto request, String avalon) throws com.fasterxml.jackson.core.JsonProcessingException {
       // FastAPI 엔드포인트 URL
       String fastApiUrl = fastApiBaseUrl + "/api/list/v1/create";
       
       // HTTP 헤더 설정
       HttpHeaders headers = new HttpHeaders();
       headers.setContentType(MediaType.APPLICATION_JSON);
       
       // avalon 쿠키를 헤더에 추가
       headers.add("Cookie", "avalon=" + avalon);
       
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
               .url(api.getUrl())
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
   
   private void saveMappingResult(MappingResponseDto response) {
       log.info("매핑 결과를 DB에 저장합니다.");

       if (response.getApiMapping() == null || response.getApiMapping().isEmpty()) {
           return;
       }

       // 1. 필요한 시나리오 ID, API 이름 목록 추출
       Set<String> scenarioIds = response.getApiMapping().stream()
           .map(ApiMappingDto::getScenarioId)
           .collect(Collectors.toSet());
       Set<String> apiNames = response.getApiMapping().stream()
           .map(ApiMappingDto::getApiName)
           .collect(Collectors.toSet());

       // 2. 한 번에 모두 조회 (쿼리 2번)
       Map<String, ScenarioEntity> scenarioMap = scenarioRepository.findByIdIn(scenarioIds)
           .stream().collect(Collectors.toMap(ScenarioEntity::getId, java.util.function.Function.identity()));
       Map<String, ApiListEntity> apiMap = apiListRepository.findByNameIn(apiNames)
           .stream().collect(Collectors.toMap(ApiListEntity::getName, java.util.function.Function.identity()));

       // 3. 매핑 엔티티 생성 (메모리에서만 처리)
       List<MappingEntity> mappingEntities = response.getApiMapping().stream()
           .map(apiMapping -> {
               ScenarioEntity scenario = scenarioMap.get(apiMapping.getScenarioId());
               ApiListEntity api = apiMap.get(apiMapping.getApiName());
               if (scenario == null) {
                   throw new BusinessExceptionHandler("시나리오를 찾을 수 없습니다: " + apiMapping.getScenarioId(), ErrorCode.NOT_FOUND_ERROR);
               }
               if (api == null) {
                   throw new BusinessExceptionHandler("API를 찾을 수 없습니다: " + apiMapping.getApiName(), ErrorCode.NOT_FOUND_ERROR);
               }
               return MappingEntity.builder()
                   .id(generateMappingId(scenario.getId(), api.getName()))
                   .step(extractStepNumber(apiMapping.getStepName()))
                   .scenarioKey(scenario)
                   .apiListKey(api)
                   .build();
           })
           .collect(Collectors.toList());

       // 4. 한 번에 저장 (쿼리 1번)
       mappingRepository.saveAll(mappingEntities);

       log.info("매핑 결과 저장 완료");
   }

   // 매핑 ID 생성
   private String generateMappingId(String scenarioId, String apiName) {
       return scenarioId + "_" + apiName;
   }

   // 단계명에서 숫자 추출
   private Integer extractStepNumber(String stepName) {
       if (stepName == null || stepName.isEmpty()) return 1;
       String numberStr = stepName.replaceAll("[^0-9]", "");
       return numberStr.isEmpty() ? 1 : Integer.parseInt(numberStr);
   }
}