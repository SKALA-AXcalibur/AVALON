package com.sk.skala.axcalibur.feature.scenario.dto.response.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiItem {
    private String id; // API 아이디
    private String name; // API 이름
    private String desc; // API 설명
}
