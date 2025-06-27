package com.sk.skala.axcalibur.feature.testcase.dto.response;

import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * TC 조회파트에서 사용되는 데이터의 파라미터 정보 DTO
 * TC에 포함되는 파라미터 데이터 전체 conext에 대한 객체 정의
 */
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TcParamDataDto extends ApiParamDto {
    private String value;
}
