package com.sk.skala.axcalibur.apitest.feature.service;

import java.text.ParseException;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTestParserServiceBuildUriRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTestParserServiceParsePreconditionRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ApiTestParserServiceParsePreconditionResponseDto;
import com.sk.skala.axcalibur.apitest.feature.repository.ApiTestDetailRepository;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiTestParserServiceImpl implements ApiTestParserService {
    private static final String HEADER = "header";
    private static final String BODY = "body";
    private static final String PATH = "path";
    private static final String QUERY = "query";

    private final ApiTestDetailRepository repo;

    @Override
    public ApiTestParserServiceParsePreconditionResponseDto parsePrecondition(
            @NotNull ApiTestParserServiceParsePreconditionRequestDto dto)
            throws ParseException {
        log.info("ApiTestParserServiceImpl.parsePrecondition called");
        log.debug("dto: {}", dto);
        HashMap<String, String> path = new HashMap<>();
        LinkedMultiValueMap<String, String> query = new LinkedMultiValueMap<>();
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        HashMap<String, Object> body = new HashMap<>();

        // 사전 조건을 , 로 분리한 다음 step으로 시작하는 부분 처리
        var parts = dto.precondition().split(",");

        // 파싱 로직
        // example: step 2:body|userId -> path|userId
        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty() || !part.startsWith("step ") || !part.contains("->")) {
                log.debug("Skipping invalid precondition part: {}", part);
                continue; // step으로 시작하지 않는 것은 무시
            }
            var steps = part.substring(5).split(":");
            int step = 0;
            try {
                step = Integer.parseInt(steps[0]);
                if (step < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                log.error("ApiTestParserServiceImpl.parsePrecondition: Invalid step: {}", steps[0]);
                throw new ParseException("Invalid step: " + steps[0], 0);
            }

            var action = steps[1].trim().split("->");
            if (action.length != 2) {
                log.error("ApiTestParserServiceImpl.parsePrecondition: Invalid action format: {}", part);
                throw new ParseException("Invalid action format: " + part, 0);
            }
            var prev = action[0].trim().split("\\|");
            var next = action[1].trim().split("\\|");

            if (prev.length < 2 || next.length < 2) {
                log.error("ApiTestParserServiceImpl.parsePrecondition: Invalid prev, next format: {}, {}", prev.length,
                        next.length);
                throw new ParseException("Invalid prev, next format: " + prev.length + ", " + next.length, 0);
            }

            var key = dto.scenarioKey() + "-" + step + "-" + dto.statusCode();
            log.debug("Looking for entity with key: {}", key);

            var entity = repo.findById(key)
                    .orElseThrow(() -> {
                        log.error("ApiTestParserServiceImpl.parsePrecondition: Scenario not found for key: {}", key);
                        return new ParseException("Not found ApiTestDetailEntity: " + key, 0);
                    });

            if (prev[0].equalsIgnoreCase(HEADER)) {
                log.debug("Processing HEADER source");
                var content = entity.getHeader();
                log.debug("Header content: {}", content);
                if (content == null || content.isEmpty()) {
                    log.debug("ApiTestParserServiceImpl.parsePrecondition: Header content is empty for key: {}", key);
                } else {
                    var data = content.get(prev[1]);
                    log.debug("Data from header key '{}': {}", prev[1], data);
                    if (data != null && !data.isEmpty()) {
                        log.debug("Processing non-null header data to target: {}", next[0]);
                        if (next[0].equalsIgnoreCase(HEADER)) {
                            header.addAll(next[1], data);
                            log.debug("Added to header: {} -> {}", next[1], data);
                        } else if (next[0].equalsIgnoreCase(BODY)) {
                            if (data.size() == 1) {
                                body.put(next[1], data.get(0));
                                log.debug("Added single value to body: {} -> {}", next[1], data.get(0));
                            } else {
                                body.put(next[1], data);
                                log.debug("Added multiple values to body: {} -> {}", next[1], data);
                            }
                        } else if (next[0].equalsIgnoreCase(PATH)) {
                            path.put(next[1], data.get(0));
                            log.debug("Added to path: {} -> {}", next[1], data.get(0));
                        } else if (next[0].equalsIgnoreCase(QUERY)) {
                            query.addAll(next[1], data);
                            log.debug("Added to query: {} -> {}", next[1], data);
                        }
                    } else {
                        log.debug("ApiTestParserServiceImpl.parsePrecondition: Header key '{}' not found for key: {}",
                                prev[1], key);
                    }
                }
            } else if (prev[0].equalsIgnoreCase(BODY)) {
                var content = entity.getBody();
                if (content == null || content.isEmpty()) {
                    log.debug("ApiTestParserServiceImpl.parsePrecondition: Body content is empty for key: {}", key);
                } else {
                    var data = content.get(prev[1]);
                    if (data != null) {
                        if (next[0].equalsIgnoreCase(HEADER)) {
                            header.add(next[1], data.toString());
                        } else if (next[0].equalsIgnoreCase(BODY)) {
                            body.put(next[1], data);
                        } else if (next[0].equalsIgnoreCase(PATH)) {
                            path.put(next[1], data.toString());
                        } else if (next[0].equalsIgnoreCase(QUERY)) {
                            query.add(next[1], data.toString());
                        }
                    } else {
                        log.debug("ApiTestParserServiceImpl.parsePrecondition: Body key '{}' not found for key: {}",
                                prev[1], key);
                    }
                }
            } else if (prev[0].equalsIgnoreCase(PATH)) {
                var content = entity.getPath();
                if (content == null || content.isEmpty()) {
                    log.debug("ApiTestParserServiceImpl.parsePrecondition: Path content is empty for key: {}", key);
                } else {
                    var data = content.get(prev[1]);
                    if (data != null) {
                        if (next[0].equalsIgnoreCase(HEADER)) {
                            header.add(next[1], data);
                        } else if (next[0].equalsIgnoreCase(BODY)) {
                            body.put(next[1], data);
                        } else if (next[0].equalsIgnoreCase(PATH)) {
                            path.put(next[1], data);
                        } else if (next[0].equalsIgnoreCase(QUERY)) {
                            query.add(next[1], data);
                        }
                    } else {
                        log.debug("ApiTestParserServiceImpl.parsePrecondition: Path key '{}' not found for key: {}",
                                prev[1], key);
                    }
                }
            } else if (prev[0].equalsIgnoreCase(QUERY)) {
                var content = entity.getQuery();
                if (content == null || content.isEmpty()) {
                    log.debug("ApiTestParserServiceImpl.parsePrecondition: Query content is empty for key: {}", key);
                } else {
                    var data = content.get(prev[1]);
                    if (data != null && !data.isEmpty()) {
                        if (next[0].equalsIgnoreCase(HEADER)) {
                            header.addAll(next[1], data);
                        } else if (next[0].equalsIgnoreCase(BODY)) {
                            if (data.size() == 1) {
                                body.put(next[1], data.get(0));
                            } else {
                                body.put(next[1], data);
                            }
                        } else if (next[0].equalsIgnoreCase(PATH)) {
                            path.put(next[1], data.get(0));
                        } else if (next[0].equalsIgnoreCase(QUERY)) {
                            query.addAll(next[1], data);
                        }
                    } else {
                        log.debug("ApiTestParserServiceImpl.parsePrecondition: Query key '{}' not found for key: {}",
                                prev[1], key);
                    }
                }
            }

        }

        return ApiTestParserServiceParsePreconditionResponseDto.builder()
                .path(path)
                .query(query)
                .header(header)
                .body(body)
                .build();
    }

    @Override
    public String buildUri(ApiTestParserServiceBuildUriRequestDto dto) {
        log.info("ApiTestParserServiceImpl.buildUri called");
        log.debug("dto: {}", dto);
        var uri = dto.uri();
        var path = dto.path();
        var query = dto.query();

        // Path 변수 치환
        for (var entry : path.entrySet()) {
            uri = uri.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        // Query 파라미터 추가
        if (!query.isEmpty()) {
            StringBuilder queryString = new StringBuilder(uri + "?");
            for (var entry : query.entrySet()) {
                for (String value : entry.getValue()) {
                    queryString.append(entry.getKey()).append("=").append(value).append("&");
                }
            }
            // 마지막 & 제거
            queryString.setLength(queryString.length() - 1);
            uri = queryString.toString();
        }

        return uri;
    }

}
