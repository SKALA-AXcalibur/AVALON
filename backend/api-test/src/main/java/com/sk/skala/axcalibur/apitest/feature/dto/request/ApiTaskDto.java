package com.sk.skala.axcalibur.apitest.feature.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Builder(toBuilder = true)
@Schema(description = "Redis Streams를 이용하기 위한 DTO")
public record ApiTaskDto(
    @NotNull @Schema(description = "시나리오 pk") Integer id,
    @NotNull @Schema(description = "테스트케이스 pk") Integer testcaseId,
    @NotNull @Schema(description = "테스트케이스 결과 pk") Integer resultId,
    @Nullable @Schema(description = "사전조건") String precondition,
    @NotNull @Schema(description = "API 실행 순서") Integer step,
    @NotNull @Schema(description = "시도 횟수") Integer attempt,
    @NotBlank @Schema(description = "HTTP Method") String method,
    @NotBlank @Schema(description = "URI") String uri,
    @NotNull @Schema(description = "요청 헤더") MultiValueMap<String, String> reqHeader,
    @NotNull @Schema(description = "요청 바디") Map<String, Object> reqBody,
    @NotNull @Schema(description = "요청 쿼리 파라미터") MultiValueMap<String, String> reqQuery,
    @NotNull @Schema(description = "요청 경로 파라미터") Map<String, String> reqPath,
    @NotNull @Schema(description = "예상 HTTP stats code. 2: 2XX, 3: 3XX, 4: 4XX, 5: 5XX") Integer statusCode,
    @NotNull @Schema(description = "예상 응답 헤더") MultiValueMap<String, String> resHeader,
    @NotNull @Schema(description = "예상 응답 바디") Map<String, Object> resBody

) implements Serializable {

  private static final long serialVersionUID = 7283948572394857231L;

  public ApiTaskDto {
    if (reqHeader == null) {
      reqHeader = new LinkedMultiValueMap<>();
    }
    if (resHeader == null) {
      resHeader = new LinkedMultiValueMap<>();
    }
    if (reqBody == null) {
      reqBody = new HashMap<>();
    }
    if (resBody == null) {
      resBody = new HashMap<>();
    }
    if (reqQuery == null) {
      reqQuery = new LinkedMultiValueMap<>();
    }
    if (reqPath == null) {
      reqPath = new HashMap<>();
    }
  }

}
