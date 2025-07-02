package com.sk.skala.axcalibur.spec.feature.project.dto;

import java.util.List;

import com.sk.skala.axcalibur.spec.feature.project.dto.item.ApiItem;
import com.sk.skala.axcalibur.spec.feature.project.dto.item.ReqItem;
import com.sk.skala.axcalibur.spec.feature.project.dto.item.TableItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


// 프로젝트 목록 저장 요청 DTO (IF-PR-0001)
// 설계서 기준: 상세한 요구사항 및 API 정보 포함
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveProjectRequestDto {
    
    private List<ReqItem> requirement;
    private List<ApiItem> apiList;
    private List<TableItem> tableList;
    
}