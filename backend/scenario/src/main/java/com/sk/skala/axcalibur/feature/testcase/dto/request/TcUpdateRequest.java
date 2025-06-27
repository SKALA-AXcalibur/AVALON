package com.sk.skala.axcalibur.feature.testcase.dto.request;

import java.util.List;

import com.sk.skala.axcalibur.feature.testcase.dto.response.TcParamDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TC 수정 DTO
 * TC의 수정 내용 DB에 반영하기 위한 요청 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TcUpdateRequest {
    private String precondition;
    private String description;
    private String expectedResult;
    
    private List<TcParamDto> testDataList;  // paramId + value
}
