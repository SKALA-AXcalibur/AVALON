package com.sk.skala.axcalibur.feature.testcase.converter;

import java.util.List;

import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;
import com.sk.skala.axcalibur.global.entity.ParameterEntity;

/**
 * API의 parameter 정보 목록 가져오는 기능
 * - 생성파트와 TC 추가파트에서 공통으로 사용하는 기능 구현체
 */
public interface ParameterConverter {
    List<ApiParamDto> toDto(List<ParameterEntity> params);
}
