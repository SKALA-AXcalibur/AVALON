package com.sk.skala.axcalibur.feature.testcase.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DB 테이블 정보 DTO
 * 테이블 정보와 관련된 DTO 객체 정의
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbTableDto {
    private String tableName;
    
    private List<DbColumnDto> colList;
}
