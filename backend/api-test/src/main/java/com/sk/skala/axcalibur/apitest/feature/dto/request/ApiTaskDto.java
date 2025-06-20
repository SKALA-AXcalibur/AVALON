package com.sk.skala.axcalibur.apitest.feature.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sk.skala.axcalibur.apitest.feature.code.ApiTestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;
import lombok.Builder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Builder
@Schema(description = "Redis Streams를 이용하기 위한 DTO")
public record ApiTaskDto(
        @NotNull @Schema(description = "테스트 진행 상태") ApiTestStatus status,
        @NotNull @Schema(description = "테스트케이스 pk") Integer id,
        @NotNull @Schema(description = "테스트케이스 결과 pk") Integer resultId,
        @Nullable @Schema(description = "사전조건") String precondition,
        @NotNull @Schema(description = "API 실행 순서") Integer step,
        @NotNull @Schema(description = "HTTP Method") String method,
        @NotNull @Schema(description = "URI") String uri,
        @Nullable @Schema(description = "요청 헤더") @JsonDeserialize(as = LinkedMultiValueMap.class) MultiValueMap<String, String> reqHeader,
        @Nullable @Schema(description = "요청 바디") Map<String, Object> reqBody,
        @NotNull @Schema(description = "예상 HTTP stats code. 2: 2XX, 3: 3XX, 4: 4XX, 5: 5XX") Integer statusCode,
        @Nullable @Schema(description = "예상 응답 헤더") @JsonDeserialize(as = LinkedMultiValueMap.class) MultiValueMap<String, String> resHeader,
        @Nullable @Schema(description = "예상 응답 바디") Map<String, Object> resBody,
        @Nullable @Schema(description = "테스트 수행 시간") Double time

) implements Serializable {

}
