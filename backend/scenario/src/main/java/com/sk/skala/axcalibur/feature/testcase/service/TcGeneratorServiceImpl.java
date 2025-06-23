package com.sk.skala.axcalibur.feature.testcase.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;
import com.sk.skala.axcalibur.feature.testcase.dto.request.TcRequestPayload;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TestcaseDataDto;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TestcaseGenerationResponse;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TestcaseParamDto;
import com.sk.skala.axcalibur.feature.testcase.entity.ApiListEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.CategoryEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.ContextEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.MappingEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.ParameterEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.ScenarioEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.TestCaseDataEntity;
import com.sk.skala.axcalibur.feature.testcase.entity.TestCaseEntity;
import com.sk.skala.axcalibur.feature.testcase.repository.CategoryRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.ContextRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.MappingRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.ParameterRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.TestCaseDataRepository;
import com.sk.skala.axcalibur.feature.testcase.repository.TestCaseRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TC 생성 단계의 서비스
 * 조합된 request 객체를 fastAPI에 보내고,
 * 생성 이후 받은 응답을 DB에 저장하는 내용을 실제 구현합니다.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class TcGeneratorServiceImpl implements TcGeneratorService {
    private final RestTemplate restTemplate;
    
    @Value("${external.fastapi.url}")
    private String fastApiBaseUrl;
    
    private final CategoryRepository categoryRepository;
    private final ContextRepository contextRepository;
    private final MappingRepository mappingRepository;
    private final ParameterRepository parameterRepository;
    private final TestCaseDataRepository testcaseDataRepository;
    private final TestCaseRepository testcaseRepository;
    
    // FastAPI로 생성 요청 전송하는 함수
    @Override
    public TestcaseGenerationResponse callFastApi(TcRequestPayload payload, ScenarioEntity scenario) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TcRequestPayload> requestEntity = new HttpEntity<>(payload, headers);

        // fastAPI 서버/api/tc/v1/{scenarioId} 로 요청 전송
        String url = UriComponentsBuilder
            .fromUriString(fastApiBaseUrl)
            .pathSegment(scenario.getScenarioId().toString())
            .build()
            .toUriString();

        log.info("sending url: {}", url);

        try {
            ResponseEntity<TestcaseGenerationResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
            );

            TestcaseGenerationResponse body = response.getBody();
            if (body == null || body.getTcList() == null) {
                throw new BusinessExceptionHandler("FastAPI 응답이 비어있습니다.", ErrorCode.IO_ERROR);
            }

            return body;
        } catch (RestClientException e) {
            log.error("FastAPI 호출 실패: {}", e.getMessage(), e);
            throw new BusinessExceptionHandler("FastAPI 호출 중 오류가 발생했습니다.", ErrorCode.IO_ERROR);
        }
    }
    
    // fastAPI로부터 생성 내용 응답 받아 저장하는 함수
    @Transactional
    @Override
    public void saveTestcases(TestcaseGenerationResponse response) {
        // category와 context 탐색 결과 저장하는 캐싱용 hashmap 정의
        Map<String, CategoryEntity> categoryCache = new HashMap<>();
        Map<String, ContextEntity> contextCache = new HashMap<>();

        // 안정적인 parent-key 탐색 위해 2-pass 방식으로 저장 진행
        for (TestcaseDataDto tcData: response.getTcList()) {
            // 매핑표 ID로 매핑 정보 조회
            MappingEntity mapping = mappingRepository.findById(tcData.getMappingId())
                .orElseThrow(() -> new BusinessExceptionHandler("매핑 정보 없음", ErrorCode.NOT_FOUND_ERROR));

            // 1. TestCase 저장
            TestCaseEntity testcase = TestCaseEntity.builder()
                .testcaseId(tcData.getTcId())
                .description(tcData.getDescription())
                .precondition(tcData.getPrecondition())
                .expected(tcData.getExpectedResult())
                .status(tcData.getStatus())
                .mappingKey(mapping)
                .build();
            testcaseRepository.save(testcase);

            // 파라미터 이름 기준 저장된 엔티티 저장할 map(상위항목 탐색 용)
            Map<String, ParameterEntity> savedParamMap = new HashMap<>();
            
            // 1st pass: ParameterEntity 먼저 저장

            // TC parameter가 없는 경우(로깅용)
            if (tcData.getTestDataList().isEmpty()) {
                log.warn("TC {}는 테스트 파라미터가 없습니다", tcData.getTcId());
            }

            for (TestcaseParamDto paramDto : tcData.getTestDataList()) {
                ApiParamDto param = paramDto.getParam();
                
                // 빈 리스트가 아닐 경우 이름, 타입에 대한 존재 여부 검증 수행
                validateParam(param);

                String categoryName = param.getCategory().trim();
                CategoryEntity category = categoryCache.computeIfAbsent( // 캐시된 카테고리 조회, 없으면 DB에서 조회
                    categoryName,
                    name -> categoryRepository.findByName(name)
                        .orElseThrow(() -> new BusinessExceptionHandler("카테고리 없음: " + name, ErrorCode.NOT_FOUND_ERROR))
                );
                
                ApiListEntity apiList = mapping.getApiListKey();

                ContextEntity context = contextCache.computeIfAbsent( // 캐시된 항목 유형 조회, 없으면 DB에서 조회
                    param.getContext(),
                    name -> contextRepository.findByName(name)
                        .orElseThrow(() -> new BusinessExceptionHandler("컨텍스트 없음", ErrorCode.NOT_FOUND_ERROR))
                );
                // key와 name으로 parameter 찾기(parameter 중복 저장 방지)
                ParameterEntity parameter;

                if (param.getParamId() != null) {
                    // paramId가 있으면 DB에서 조회
                    parameter = parameterRepository.findById(param.getParamId())
                        .orElseThrow(() -> new BusinessExceptionHandler("파라미터 ID가 유효하지 않습니다: " + param.getParamId(), ErrorCode.NOT_FOUND_ERROR));
                } else {
                    // 없으면 name 기준 새 parameter 항목 생성
                    parameter = parameterRepository.findByApiListKey_IdAndName(apiList.getId(), param.getName())
                        .orElseGet(() -> parameterRepository.save(
                            ParameterEntity.builder()
                                .nameKo(param.getKoName())
                                .name(param.getName())
                                .dataType(param.getType())
                                .length(param.getLength())
                                .format(param.getFormat())
                                .defaultValue(param.getDefaultValue())
                                .required(param.getRequired())
                                .description(param.getDesc())
                                .categoryKey(category)
                                .contextKey(context)
                                .apiListKey(apiList)
                                .parentKey(null)
                                .build()
                        ));
                }
                
                // 저장된 parameter는 이후 상위항목 조회를 위해 map에 저장
                savedParamMap.put(param.getName(), parameter);
            }
            
            // 2nd pass: parentKey 연결 및 TestCaseDataEntity 저장
            for (TestcaseParamDto paramDto : tcData.getTestDataList()) {
                ApiParamDto param = paramDto.getParam();
                ParameterEntity parameter = savedParamMap.get(param.getName());

                if (param.getParent() != null) {
                    ParameterEntity parent = savedParamMap.get(param.getParent());
                    if (parameter.getParentKey() == null && parent != null) {
                        parameter.setParentKey(parent);
                        parameterRepository.save(parameter); // 다시 저장하여 parentKey 반영
                    }
                }

                testcaseDataRepository.save(
                    TestCaseDataEntity.builder()
                        .testcaseKey(testcase)
                        .parameterKey(parameter)
                        .value(paramDto.getValue())
                        .build()
                );
            }
        }
    }
    
    // Not Null이여야되는 파라미터 항목 여부 검증
    private void validateParam(ApiParamDto param) {
        if (param.getName() == null || param.getName().isBlank()) {
            throw new BusinessExceptionHandler("파라미터 name 누락", ErrorCode.BAD_REQUEST_ERROR);
        }
        if (param.getType() == null) {
            throw new BusinessExceptionHandler("파라미터 type 누락", ErrorCode.BAD_REQUEST_ERROR);
        }
        if (param.getRequired() == null) {
            throw new BusinessExceptionHandler("파라미터 필수 여부 누락", ErrorCode.BAD_REQUEST_ERROR);
        }
    }
}
