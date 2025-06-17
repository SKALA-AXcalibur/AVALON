package com.sk.skala.axcalibur.spec.feature.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

import com.sk.skala.axcalibur.spec.feature.project.dto.item.ColItem;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableInfoDto {
    private String name; // 테이블명
    private List<ColItem> column; // 컬럼 목록
} 