package com.sk.skala.axcalibur.feature.testcase.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ParameterEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;

/**
 * API의 parameter 정보 목록 가져오는 기능
 * - 생성파트와 TC 추가파트에서 공통으로 사용하는 기능 모듈화
 */
@Component
public class ParameterConverterImpl implements ParameterConverter {
    @Override
    public List<ApiParamDto> toDto(List<ParameterEntity> params) {
        return params.stream()
            .map(param -> {
                if (param.getCategoryKey() == null || param.getContextKey() == null) {
                    throw new BusinessExceptionHandler(
                        "파라미터의 category/context 정보가 유효하지 않습니다.",
                        ErrorCode.NOT_VALID_ERROR);
                }
                return ApiParamDto.builder()
                    .paramId(param.getId())
                    .category(param.getCategoryKey().getName())
                    .koName(param.getNameKo())
                    .name(param.getName())
                    .context(param.getContextKey().getName())
                    .type(param.getDataType())
                    .length(param.getLength())
                    .format(param.getFormat())
                    .defaultValue(param.getDefaultValue())
                    .required(param.getRequired())
                    .parent(param.getParentKey() != null ? param.getParentKey().getName() : null)
                    .desc(param.getDescription())
                    .build();
            }).collect(Collectors.toList());
    }
}
