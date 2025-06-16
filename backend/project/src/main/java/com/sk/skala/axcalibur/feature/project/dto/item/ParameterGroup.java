package com.sk.skala.axcalibur.feature.project.dto.item;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterGroup {
    // Step 4에서 명세서의 pq, req, res 객체들을 표현
    private List<ParameterItem> pq;       // pathQuery 파라미터 객체들
    private List<ParameterItem> req;      // request 파라미터 객체들  
    private List<ParameterItem> res;      // response 파라미터 객체들
}