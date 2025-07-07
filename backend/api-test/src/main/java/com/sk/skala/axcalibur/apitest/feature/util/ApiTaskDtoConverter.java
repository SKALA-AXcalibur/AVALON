package com.sk.skala.axcalibur.apitest.feature.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import com.sk.skala.axcalibur.apitest.global.code.ErrorCode;
import com.sk.skala.axcalibur.apitest.global.exception.BusinessExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

/**
 * ApiTaskDto와 Map<String, String> 간의 변환을 담당하는 유틸리티 클래스
 * Redis Stream에서 MapRecord 사용을 위한 직렬화/역직렬화 처리
 * 
 * 제네릭 메서드 활용:
 * 
 * 1. TypeReference:
 * TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String,
 * Object>>() {};
 * Map<String, Object> result = deserializeToType(json, typeRef);
 * 
 * 2. Class 타입:
 * MyClass result = deserializeToClass(json, MyClass.class);
 * 
 */
@Component
@Slf4j
public class ApiTaskDtoConverter {

  private static ObjectMapper mapper;
  // private static final Logger log =
  // LoggerFactory.getLogger(ApiTaskDtoConverter.class);

  // 다양한 타입을 위한 TypeReference들
  private static final TypeReference<Map<String, Object>> objectMapTypeRef = new TypeReference<Map<String, Object>>() {
  };
  private static final TypeReference<Map<String, String>> stringMapTypeRef = new TypeReference<Map<String, String>>() {
  };

  public ApiTaskDtoConverter(ObjectMapper objectMapper) {
    mapper = objectMapper;
    log.info("ApiTaskDtoConverter: ObjectMapper successfully injected via constructor");
  }

  /**
   * ApiTaskDto를 Map<String, String>으로 변환
   * Redis Stream MapRecord 전송을 위한 메서드
   */
  public static Map<String, String> toMap(ApiTaskDto dto) {
    log.info("ApiTaskDtoConverter.toMap: Converting ApiTaskDto to Map: {}", dto);
    Map<String, String> map = new HashMap<>();

    map.put("id", String.valueOf(dto.id()));
    map.put("testcaseId", String.valueOf(dto.testcaseId()));
    map.put("attempt", String.valueOf(dto.attempt()));
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
        map.put("reqHeader", mapper.writeValueAsString(dto.reqHeader()));
      } catch (JsonProcessingException e) {
        log.error("ApiTaskDtoConverter.toMap: Failed to serialize reqHeader: {}", e.getMessage());
        throw new BusinessExceptionHandler("ApiTaskDtoConverter.toMap: Failed to serialize reqHeader",
            ErrorCode.JACKSON_PROCESS_ERROR, e);
      }
    }

    if (dto.reqBody() != null) {
      try {
        map.put("reqBody", mapper.writeValueAsString(dto.reqBody()));
      } catch (JsonProcessingException e) {
        log.error("ApiTaskDtoConverter.toMap: Failed to serialize reqBody: {}", e.getMessage());
        throw new BusinessExceptionHandler("ApiTaskDtoConverter.toMap: Failed to serialize reqBody",
            ErrorCode.JACKSON_PROCESS_ERROR, e);
      }
    }

    if (dto.resHeader() != null) {
      try {
        map.put("resHeader", mapper.writeValueAsString(dto.resHeader()));
      } catch (JsonProcessingException e) {
        log.error("ApiTaskDtoConverter.toMap: Failed to serialize resHeader: {}", e.getMessage());
        throw new BusinessExceptionHandler("ApiTaskDtoConverter.toMap: Failed to serialize resHeader",
            ErrorCode.JACKSON_PROCESS_ERROR, e);
      }
    }

    if (dto.resBody() != null) {
      try {
        map.put("resBody", mapper.writeValueAsString(dto.resBody()));
      } catch (JsonProcessingException e) {
        log.error("ApiTaskDtoConverter.toMap: Failed to serialize resBody: {}", e.getMessage());
        throw new BusinessExceptionHandler("ApiTaskDtoConverter.toMap: Failed to serialize resBody",
            ErrorCode.JACKSON_PROCESS_ERROR, e);
      }
    }

    if (dto.reqPath() != null) {
      try {
        map.put("reqPath", mapper.writeValueAsString(dto.reqPath()));
      } catch (JsonProcessingException e) {
        log.error("ApiTaskDtoConverter.toMap: Failed to serialize reqPath: {}", e.getMessage());
        throw new BusinessExceptionHandler("ApiTaskDtoConverter.toMap: Failed to serialize reqPath",
            ErrorCode.JACKSON_PROCESS_ERROR, e);
      }
    }

    if (dto.reqQuery() != null) {
      try {
        map.put("reqQuery", mapper.writeValueAsString(dto.reqQuery()));
      } catch (JsonProcessingException e) {
        log.error("ApiTaskDtoConverter.toMap: Failed to serialize reqQuery: {}", e.getMessage());
        throw new BusinessExceptionHandler("ApiTaskDtoConverter.toMap: Failed to serialize reqQuery",
            ErrorCode.JACKSON_PROCESS_ERROR, e);
      }
    }

    return map;
  }

  /**
   * Map<String, String>을 ApiTaskDto로 변환
   * Redis Stream MapRecord 수신 후 역직렬화를 위한 메서드
   */
  public static ApiTaskDto fromMap(Map<String, String> map) {
    log.info("ApiTaskDtoConverter.fromMap: Converting Map to ApiTaskDto: {}", map);
    try {
      return ApiTaskDto.builder()
          .id(parseInteger(map.get("id")))
          .testcaseId(parseInteger(map.get("testcaseId")))
          .resultId(parseInteger(map.get("resultId")))
          .step(parseInteger(map.get("step")))
          .attempt(parseInteger(map.get("attempt")))
          .method(cleanString(map.get("method")))
          .uri(cleanString(map.get("uri")))
          .statusCode(parseInteger(map.get("statusCode")))
          .precondition(cleanString(map.get("precondition")))
          .reqHeader(deserializeMultiValueMap(map.get("reqHeader")))
          .reqBody(deserializeMap(map.get("reqBody")))
          .resHeader(deserializeMultiValueMap(map.get("resHeader")))
          .resBody(deserializeMap(map.get("resBody")))
          .reqPath(deserializeStringMap(map.get("reqPath")))
          .reqQuery(deserializeMultiValueMap(map.get("reqQuery")))
          .build();
    } catch (PatternSyntaxException e) {
      log.error("ApiTaskDtoConverter.fromMap: Failed to convert map to ApiTaskDto: {}", e.getMessage());
      throw new BusinessExceptionHandler("ApiTaskDtoConverter.fromMap: Failed to convert map to ApiTaskDto",
          ErrorCode.DESERIALIZE_ERROR, e);
    }
  }

  /**
   * 문자열을 Integer로 안전하게 파싱 (이중 따옴표 제거 포함)
   */
  private static Integer parseInteger(String value) throws PatternSyntaxException {
    log.info("ApiTaskDtoConverter.parseInteger: Parsing value to Integer: {}", value);
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    // 이중 따옴표가 있는 경우 제거
    String cleanValue = cleanString(value);
    return Integer.valueOf(cleanValue);
  }

  /**
   * 문자열에서 이중 따옴표 제거
   */
  private static String cleanString(String value) throws PatternSyntaxException {
    log.info("ApiTaskDtoConverter.cleanString: Cleaning string value: {}", value);
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
    log.info("ApiTaskDtoConverter.deserializeMultiValueMap: Deserializing JSON to MultiValueMap: {}", json);
    if (json == null || json.trim().isEmpty()) {
      return new LinkedMultiValueMap<>();
    }

    try {
      // 제네릭 범용 메서드를 활용
      Map<String, Object> tempMap = deserializeToType(json, objectMapTypeRef);
      if (tempMap == null) {
        tempMap = new HashMap<>();
      }

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
    } catch (Exception e) {
      log.error("ApiTaskDtoConverter.deserializeMultiValueMap: Failed to deserialize MultiValueMap: {}",
          e.getMessage());
      return null;
    }
  }

  /**
   * JSON 문자열을 Map<String, Object>으로 역직렬화
   */
  private static Map<String, Object> deserializeMap(String json) {
    log.info("ApiTaskDtoConverter.deserializeMap: Deserializing JSON to Map<String, Object>: {}", json);
    Map<String, Object> map = deserializeToType(json, objectMapTypeRef);
    if (map == null) {
      map = new HashMap<>();
    }
    return map;
  }

  /**
   * 제네릭을 활용한 범용 역직렬화 메서드
   * TypeReference를 받아서 원하는 타입으로 역직렬화
   */
  private static <T> T deserializeToType(String json, TypeReference<T> typeReference) {
    log.info("ApiTaskDtoConverter.deserializeToType: Deserializing JSON to type {}: {}",
        typeReference.getType(), json);
    if (json == null || json.trim().isEmpty()) {
      log.warn("ApiTaskDtoConverter.deserializeToType: JSON is null or empty, returning null");
      return null;
    }

    try {
      // 문자열 시작이 홑따옴표인 경우 제거
      if (json.startsWith("'")) {
        json = json.substring(1);
      }
      // 문자열 끝이 홑따옴표인 경우 제거
      if (json.endsWith("'")) {
        json = json.substring(0, json.length() - 1);
      }
      json = unescapeJson(json);
      return mapper.readValue(json, typeReference);
    } catch (JsonProcessingException e) {
      log.error("ApiTaskDtoConverter.deserializeToType: Failed to deserialize to type {}: {}",
          typeReference.getType(), e.getMessage());
      return null;
    }
  }

  /**
   * JSON 이스케이프 해제
   * 
   * @param json
   * @return
   */
  private static String unescapeJson(String json) {
    if (json == null)
      return null;

    // 앞뒤 따옴표 제거
    if (json.startsWith("\"") && json.endsWith("\"")) {
      json = json.substring(1, json.length() - 1);
    }

    // JSON 이스케이프 해제
    return json.replace("\\\"", "\"")
        .replace("\\\\", "\\");
  }

  /**
   * Map<String, String> 전용 역직렬화 메서드
   */
  private static Map<String, String> deserializeStringMap(String json) {
    log.info("ApiTaskDtoConverter.deserializeStringMap: Deserializing JSON to Map<String, String>: {}", json);
    Map<String, String> map = deserializeToType(json, stringMapTypeRef);
    if (map == null) {
      map = new HashMap<>();
    }
    return map;
  }

}