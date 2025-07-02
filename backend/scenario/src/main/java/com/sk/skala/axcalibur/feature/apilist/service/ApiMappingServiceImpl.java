package com.sk.skala.axcalibur.feature.apilist.service;

import com.sk.skala.axcalibur.feature.apilist.dto.ApiDto;
import com.sk.skala.axcalibur.feature.apilist.dto.ApiMappingResponseDto;
import com.sk.skala.axcalibur.feature.apilist.dto.MappingRequestDto;
import com.sk.skala.axcalibur.feature.apilist.dto.MappingResponseDto;
import com.sk.skala.axcalibur.feature.apilist.dto.ScenarioDto;
import com.sk.skala.axcalibur.feature.apilist.repository.ApiListRepository;
import com.sk.skala.axcalibur.feature.apilist.repository.AvalonCookieRepository;
import com.sk.skala.axcalibur.feature.apilist.repository.MappingRepository;
import com.sk.skala.axcalibur.feature.apilist.repository.ScenarioRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.AvalonCookieEntity;
import com.sk.skala.axcalibur.global.entity.ApiListEntity;
import com.sk.skala.axcalibur.global.entity.MappingEntity;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;

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

import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;

import jakarta.annotation.PostConstruct;

@Slf4j
@Service
public class ApiMappingServiceImpl implements ApiMappingService {
    
    @Value("${external.fastapi.url}")
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
    * API 매핑 (avalon 값으로 매핑 및 결과 반환)
    * @param avalon 프로젝트 ID
    * @return MappingResponseDto 매핑 결과 데이터
    */
   @Override
   public MappingResponseDto doApiMapping(String avalon) throws com.fasterxml.jackson.core.JsonProcessingException {
       log.info("API 매핑 시작");

       // DB에서 시나리오/Api 목록을 조회해서 매핑 요청 DTO 생성
       MappingRequestDto request = getMappingRequestDtoByAvalon(avalon);

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
           throw new BusinessExceptionHandler("요청 데이터가 없습니다.", ErrorCode.BAD_REQUEST_ERROR);
       }

       if (request.getScenarioList() == null || request.getScenarioList().isEmpty()) {
           throw new BusinessExceptionHandler("시나리오 목록이 없습니다.", ErrorCode.BAD_REQUEST_ERROR);
       }

       if (request.getApiList() == null || request.getApiList().isEmpty()) {
           throw new BusinessExceptionHandler("API 목록이 없습니다.", ErrorCode.BAD_REQUEST_ERROR);
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
       
       try {
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
           
       } catch (org.springframework.web.client.HttpClientErrorException e) {
           log.error("FastAPI 클라이언트 에러 (4xx): {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
           throw new BusinessExceptionHandler("FastAPI 서버에서 클라이언트 에러가 발생했습니다: " + e.getStatusCode(), ErrorCode.BAD_REQUEST_ERROR);
           
       } catch (org.springframework.web.client.HttpServerErrorException e) {
           log.error("FastAPI 서버 에러 (5xx): {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
           throw new BusinessExceptionHandler("FastAPI 서버에서 내부 에러가 발생했습니다: " + e.getStatusCode(), ErrorCode.INTERNAL_SERVER_ERROR);
           
       } catch (org.springframework.web.client.ResourceAccessException e) {
           log.error("FastAPI 서버 연결 실패: {}", e.getMessage());
           throw new BusinessExceptionHandler("FastAPI 서버에 연결할 수 없습니다: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
           
       } catch (Exception e) {
           log.error("FastAPI 호출 중 예상치 못한 에러 발생: {}", e.getMessage(), e);
           throw new BusinessExceptionHandler("FastAPI 호출 중 오류가 발생했습니다: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
       }
   }

   private MappingResponseDto createResponse(MappingResponseDto response) {
       log.info("매핑 결과 생성");
       return response;
   }

   // ScenarioEntity → ScenarioDto 변환
   private List<ScenarioDto> convertToScenarioDtos(List<ScenarioEntity> scenarios) {
       return scenarios.stream()
           .map(scenario -> ScenarioDto.builder()
               .scenarioId(String.valueOf(scenario.getId()))
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
       List<ScenarioEntity> scenarios = scenarioRepository.findByProjectId(projectKey);

       // 프로젝트 기준으로 API 목록 조회
       List<ApiListEntity> apis = apiListRepository.findByProjectKey_Id(projectKey);

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
       Set<Integer> scenarioIds = response.getApiMapping().stream()
           .map(apiMapping -> Integer.parseInt(apiMapping.getScenarioId()))
           .collect(Collectors.toSet());
       Set<String> apiNames = response.getApiMapping().stream()
           .map(ApiMappingResponseDto::getApiName)
           .collect(Collectors.toSet());

       // 2. 한 번에 모두 조회 (쿼리 2번)
       Map<String, ScenarioEntity> scenarioMap = scenarioRepository.findByIdIn(scenarioIds)
           .stream().collect(Collectors.toMap(scenario -> String.valueOf(scenario.getId()), java.util.function.Function.identity()));
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

   // 단계명에서 숫자 추출
   private Integer extractStepNumber(String stepName) {
       if (stepName == null || stepName.isEmpty()) return 1;
       String numberStr = stepName.replaceAll("[^0-9]", "");
       return numberStr.isEmpty() ? 1 : Integer.parseInt(numberStr);
   }
}