package com.sk.skala.axcalibur.feature.dto.item;

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
    private ParameterItem pq;       // pathQuery 파라미터 객체들
    private ParameterItem req;      // request 파라미터 객체들  
    private ParameterItem res;      // response 파라미터 객체들
}