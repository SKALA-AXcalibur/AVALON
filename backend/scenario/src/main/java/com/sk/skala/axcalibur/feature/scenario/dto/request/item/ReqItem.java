package com.sk.skala.axcalibur.feature.scenario.dto.request.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqItem {

    private String reqId;       // 요구사항 아이디
    private String name;        // 요구사항 이름
    private String desc;        // 요구사항 설명  
    private String priority;    // 요구사항 우선도
    private String major;       // 요구사항 대분류
    private String middle;      // 요구사항 중분류
    private String minor;       // 요구사항 소분류
}
