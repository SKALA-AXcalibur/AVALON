package com.sk.skala.axcalibur.apitest.feature.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

/**
 * ApiTaskDto와 Map<String, String> 간의 변환을 담당하는 유틸리티 클래스
 * Redis Stream에서 MapRecord 사용을 위한 직렬화/역직렬화 처리
 */
public class ApiTaskDtoConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(ApiTaskDtoConverter.class);

    /**
     * ApiTaskDto를 Map<String, String>으로 변환
     * Redis Stream MapRecord 전송을 위한 메서드
     */
    public static Map<String, String> toMap(ApiTaskDto dto) {
        log.debug("ApiTaskDtoConverter.toMap: Converting ApiTaskDto to Map: {}", dto);
        Map<String, String> map = new HashMap<>();

        map.put("id", String.valueOf(dto.id()));
        map.put("resultId", String.valueOf(dto.resultId()));
        map.put("step", String.valueOf(dto.step()));
        map.put("method", dto.method());
        map.put("uri", dto.uri());
        map.put("statusCode", String.valueOf(dto.statusCode()));

        // null 체크 후 직렬화
        if (dto.precondition() != null) {
            map.put("precondition", dto.precondition());
        }

        if (dto.reqHeader() != null) {
            try {
                map.put("reqHeader", objectMapper.writeValueAsString(dto.reqHeader()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize reqHeader: {}", e.getMessage());
                throw new RuntimeException("Failed to serialize reqHeader", e);
            }
        }

        if (dto.reqBody() != null) {
            try {
                map.put("reqBody", objectMapper.writeValueAsString(dto.reqBody()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize reqBody: {}", e.getMessage());
                throw new RuntimeException("Failed to serialize reqBody", e);
            }
        }

        if (dto.resHeader() != null) {
            try {
                map.put("resHeader", objectMapper.writeValueAsString(dto.resHeader()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize resHeader: {}", e.getMessage());
                throw new RuntimeException("Failed to serialize resHeader", e);
            }
        }

        if (dto.resBody() != null) {
            try {
                map.put("resBody", objectMapper.writeValueAsString(dto.resBody()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize resBody: {}", e.getMessage());
                throw new RuntimeException("Failed to serialize resBody", e);
            }
        }

        return map;
    }

    /**
     * Map<String, String>을 ApiTaskDto로 변환
     * Redis Stream MapRecord 수신 후 역직렬화를 위한 메서드
     */
    public static ApiTaskDto fromMap(Map<String, String> map) {
        log.debug("ApiTaskDtoConverter.fromMap: Converting Map to ApiTaskDto: {}", map);
        try {
            return ApiTaskDto.builder()
                    .id(parseInteger(map.get("id")))
                    .resultId(parseInteger(map.get("resultId")))
                    .step(parseInteger(map.get("step")))
                    .method(cleanString(map.get("method")))
                    .uri(cleanString(map.get("uri")))
                    .statusCode(parseInteger(map.get("statusCode")))
                    .precondition(cleanString(map.get("precondition")))
                    .reqHeader(deserializeMultiValueMap(map.get("reqHeader")))
                    .reqBody(deserializeMap(map.get("reqBody")))
                    .resHeader(deserializeMultiValueMap(map.get("resHeader")))
                    .resBody(deserializeMap(map.get("resBody")))
                    .build();
        } catch (Exception e) {
            log.error("Failed to convert map to ApiTaskDto: {}", e.getMessage());
            throw new RuntimeException("Failed to convert map to ApiTaskDto", e);
        }
    }

    /**
     * 문자열을 Integer로 안전하게 파싱 (이중 따옴표 제거 포함)
     */
    private static Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        // 이중 따옴표가 있는 경우 제거
        String cleanValue = value.replaceAll("^\"|\"$", "");
        return Integer.valueOf(cleanValue);
    }

    /**
     * 문자열에서 이중 따옴표 제거
     */
    private static String cleanString(String value) {
        if (value == null) {
            return null;
        }
        // 이중 따옴표가 있는 경우 제거
        return value.replaceAll("^\"|\"$", "");
    }

    /**
     * JSON 문자열을 MultiValueMap으로 역직렬화
     */
    private static MultiValueMap<String, String> deserializeMultiValueMap(String json) {
        log.debug("ApiTaskDtoConverter.deserializeMultiValueMap: Deserializing JSON to MultiValueMap: {}", json);
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            // @SuppressWarnings("unchecked")
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {
            };
            Map<String, Object> tempMap = objectMapper.readValue(json, typeRef);
            MultiValueMap<String, String> result = new LinkedMultiValueMap<>();

            tempMap.forEach((key, value) -> {
                if (value instanceof Iterable) {
                    for (Object item : (Iterable<?>) value) {
                        result.add(key, String.valueOf(item));
                    }
                } else {
                    result.add(key, String.valueOf(value));
                }
            });

            return result;
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize MultiValueMap: {}", e.getMessage());
            return null;
        }
    }

    /**
     * JSON 문자열을 Map<String, Object>으로 역직렬화
     */
    private static Map<String, Object> deserializeMap(String json) {
        log.debug("ApiTaskDtoConverter.deserializeMap: Deserializing JSON to Map: {}", json);
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(json, Map.class);
            return result;
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize Map: {}", e.getMessage());
            return null;
        }
    }
}
