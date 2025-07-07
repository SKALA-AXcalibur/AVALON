package com.sk.skala.axcalibur.feature.scenario.dto.response.item;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiItem {
    @JsonProperty("id")
    private String id; // API 아이디
    @JsonProperty("name")
    private String name; // API 이름
    @JsonProperty("desc")
    private String desc; // API 설명
}
