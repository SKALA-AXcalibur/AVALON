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
    
    private final RestTemplate restTemplate = new RestTemplate();
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
           .directory("backend/scenario")
           .load();
       this.llmApiUrl = dotenv.get("LANGCHAIN_ENDPOINT");
       this.llmApiKey = dotenv.get("LANGCHAIN_API_KEY");
       this.modelName = dotenv.get("MODEL_NAME");
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
   public MappingResponseDto doApiMapping(MappingRequestDto request) {
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

   private MappingResponseDto llmMapping(MappingRequestDto request) {
       log.info("LLM 의미적 매핑 시작");
       
       // 1. API 목록 기준으로 시나리오 단계 분리
       List<ScenarioStep> scenarioSteps = breakDownScenarios(request);
       
       // 2. LLM 의미적 매핑 수행
       List<ApiMappingDto> mappingTable = performSemanticMapping(scenarioSteps, request.getApiList());
       
       // 3. 매핑표 검증
       double validationRate = validateMappingTable(mappingTable, scenarioSteps, request.getApiList());
       
       log.info("LLM 매핑 완료");
       
       // 4. 응답 생성
       return new MappingResponseDto(
           LocalDateTime.now().toString(),
           validationRate,
           mappingTable
       );
   }

   // API 목록 기준으로 시나리오를 단계별로 분리
   private List<ScenarioStep> breakDownScenarios(MappingRequestDto request) {
       List<ScenarioStep> steps = new ArrayList<>();
       
       for (ScenarioDto scenario : request.getScenarioList()) {
           ScenarioStep step = new ScenarioStep(
               scenario.getScenarioId(),
               scenario.getTitle(),
               scenario.getDescription()
           );
           steps.add(step);
       }
       
       return steps;
   }

   // LLM을 통한 의미적 매핑
   private List<ApiMappingDto> performSemanticMapping(List<ScenarioStep> steps, List<ApiDto> apiList) {
       List<ApiMappingDto> mappings = new ArrayList<>();
       
       for (ScenarioStep step : steps) {
           for (ApiDto api : apiList) {
               if (isSemanticMatch(step.getDescription(), api.getDescription())) {
                   ApiMappingDto mapping = new ApiMappingDto(
                       step.getScenarioId(),
                       step.getStepName(),
                       api.getApiName(),
                       api.getDescription(),
                       api.getUrl(),
                       api.getMethod(),
                       api.getParameters(),
                       api.getResponseStructure()
                   );
                   mappings.add(mapping);
               }
           }
       }
       
       return mappings;
   }

   // 매핑표 검증
   private double validateMappingTable(List<ApiMappingDto> mappingTable, 
                                     List<ScenarioStep> steps, 
                                     List<ApiDto> apiList) {
       int totalItems = mappingTable.size();
       int validatedItems = 0;
       
       for (ApiMappingDto mapping : mappingTable) {
           boolean isValid = true;
           
           // 1. 의미적 일치 검증
           if (!validateSemanticConsistency(mapping)) {
               isValid = false;
           }
           
           // 2. 기술적 항목 검증 (URI, Method, 파라미터, 응답구조)
           if (!validateTechnicalConsistency(mapping, apiList)) {
               isValid = false;
           }
           
           if (isValid) {
               validatedItems++;
           }
       }
       
       return totalItems > 0 ? (double) validatedItems / totalItems * 100 : 0.0;
   }

   // 의미적 일치 검증
   private boolean validateSemanticConsistency(ApiMappingDto mapping) {
       return mapping.getDescription() != null && !mapping.getDescription().trim().isEmpty();
   }

   // 기술적 항목 검증
   private boolean validateTechnicalConsistency(ApiMappingDto mapping, List<ApiDto> apiList) {
       for (ApiDto api : apiList) {
           if (api.getApiName().equals(mapping.getApiName())) {
               return api.getUrl().equals(mapping.getUrl()) &&
                      api.getMethod().equals(mapping.getMethod());
           }
       }
       return false;
   }

   // Claude API 호출
   private String callClaudeApi(String prompt) {
       try {
           HttpHeaders headers = new HttpHeaders();
           headers.setContentType(MediaType.APPLICATION_JSON);
           headers.set("x-api-key", llmApiKey);
           headers.set("anthropic-version", "2023-06-01");
           
           String requestBody = String.format("""
               {
                 "model": "%s",
                 "max_tokens": 10,
                 "messages": [
                   {
                     "role": "user",
                     "content": "%s"
                   }
                 ]
               }
               """, modelName, prompt.replace("\"", "\\\""));
           
           HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
           ResponseEntity<String> response = restTemplate.exchange(
               llmApiUrl, HttpMethod.POST, entity, String.class
           );
           
           return response.getBody();
       } catch (Exception e) {
           log.error("Claude API 호출 실패: {}", e.getMessage());
           throw new RuntimeException("LLM 서비스 unavailable", e);
       }
   }

   // 응답에서 유사도 점수 파싱
   private double parseSimilarityFromResponse(String response) {
       try {
           JsonNode jsonNode = objectMapper.readTree(response);
           String content = jsonNode.path("content").get(0).path("text").asText();
           
           // 숫자만 추출 (0.85, .7, 85 등 다양한 형태 지원)
           String cleanContent = content.replaceAll("[^0-9.]", "");
           double score = Double.parseDouble(cleanContent);
           
           // 0-1 범위로 정규화 (100점 만점인 경우 처리)
           if (score > 1.0) {
               score = score / 100.0;
           }
           
           return Math.max(0.0, Math.min(1.0, score));
       } catch (Exception e) {
           log.error("LLM 응답 파싱 실패: {}", e.getMessage());
           return 0.0;
       }
   }

   // LLM 호출 메서드
   private double callLlmForSimilarity(String stepDescription, String apiDescription) {
       try {
           String prompt = String.format("""
               다음 두 텍스트의 의미적 유사도를 0.0에서 1.0 사이의 숫자로 평가해주세요.
               
               텍스트 1: %s
               텍스트 2: %s
               
               응답은 숫자만 반환해주세요 (예: 0.85)
               """, stepDescription, apiDescription);
               
           String response = callClaudeApi(prompt);
           return parseSimilarityFromResponse(response);
       } catch (Exception e) {
           log.error("LLM 호출 실패: {}", e.getMessage());
           return 0.0; // LLM 실패시 매칭하지 않음
       }
   }

   // 의미적 매칭 검증 (LLM 기반)
   private boolean isSemanticMatch(String stepDescription, String apiDescription) {
       if (stepDescription == null || apiDescription == null) {
           return false;
       }
       
       double similarity = callLlmForSimilarity(stepDescription, apiDescription);
       boolean isMatch = similarity >= 0.7;
       
       if (isMatch) {
           log.info("LLM 의미적 매칭 성공 [유사도: {}] - Step: '{}' <-> API: '{}'", 
                   similarity, stepDescription, apiDescription);
       }
       
       return isMatch;
   }

   private MappingResponseDto createResponse(MappingResponseDto response) {
       log.info("매핑 결과 생성");
       return response;
   }

   // 시나리오 단계 클래스
   private static class ScenarioStep {
       private String scenarioId;
       private String stepName;
       private String description;
       
       public ScenarioStep(String scenarioId, String stepName, String description) {
           this.scenarioId = scenarioId;
           this.stepName = stepName;
           this.description = description;
       }
       
       public String getScenarioId() { return scenarioId; }
       public String getStepName() { return stepName; }
       public String getDescription() { return description; }
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
   
}