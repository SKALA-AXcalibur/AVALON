package com.sk.skala.axcalibur.feature.testcase.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;
import com.sk.skala.axcalibur.feature.testcase.dto.request.TcRequestPayload;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TestcaseDataDto;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TestcaseGenerationResponse;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TestcaseParamDto;
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
    
    @Override
    public TestcaseGenerationResponse callFastApi(TcRequestPayload payload, ScenarioEntity scenario) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<TcRequestPayload> requestEntity = new HttpEntity<>(payload, headers);

        String url = String.format("%s/%s", fastApiBaseUrl, scenario.getScenarioId());
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
    
    @Override
    public void saveTestcases(TestcaseGenerationResponse response) {
        Map<String, CategoryEntity> categoryCache = new HashMap<>();
        Map<String, ContextEntity> contextCache = new HashMap<>();

        for (TestcaseDataDto tcData: response.getTcList()) {
            // 매핑표 ID로 매핑 정보 조회
            MappingEntity mapping = mappingRepository.findById(tcData.getMappingId())
                .orElseThrow(() -> new BusinessExceptionHandler("매핑 정보 없음", ErrorCode.NOT_FOUND_ERROR));

            // 1. TestCase 저장
            TestCaseEntity testcase = TestCaseEntity.builder()
                .id(tcData.getTcId())
                .description(tcData.getDescription())
                .precondition(tcData.getPrecondition())
                .expected(tcData.getExpectedResult())
                .mappingKey(mapping)
                .build();
            testcaseRepository.save(testcase);

            // 파라미터 이름 기준 저장된 엔티티 저장할 map(상위항목)
            Map<String, ParameterEntity> savedParamMap = new HashMap<>();

            for (TestcaseParamDto paramDto : tcData.getTestDataList()) {
                ApiParamDto param = paramDto.getParam();

                CategoryEntity category = categoryCache.computeIfAbsent(
                    param.getCategory(),
                    name -> categoryRepository.findByName(name)
                        .orElseThrow(() -> new BusinessExceptionHandler("카테고리 없음", ErrorCode.NOT_FOUND_ERROR))
                );
                ContextEntity context = contextCache.computeIfAbsent(
                    param.getContext(),
                    name -> contextRepository.findByName(name)
                        .orElseThrow(() -> new BusinessExceptionHandler("컨텍스트 없음", ErrorCode.NOT_FOUND_ERROR))
                );
                
                // 상위 항목 처리
                ParameterEntity parent = param.getParent() != null
                    ? savedParamMap.get(param.getParent()) : null;

                ParameterEntity parameter = parameterRepository.save(
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
                        .apiListKey(mapping.getApiListKey())
                        .parentKey(parent)
                        .build()
                );

                savedParamMap.put(param.getName(), parameter);

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
}
