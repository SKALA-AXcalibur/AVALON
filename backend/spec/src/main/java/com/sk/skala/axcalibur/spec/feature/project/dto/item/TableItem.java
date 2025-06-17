package com.sk.skala.axcalibur.spec.feature.project.dto.item;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableItem {
    private String name; // 테이블명
    private List<ColItem> column; // 컬럼 목록
}
