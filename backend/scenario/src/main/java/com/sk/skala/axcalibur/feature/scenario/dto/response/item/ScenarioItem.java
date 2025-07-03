package com.sk.skala.axcalibur.feature.scenario.dto.response.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioItem {
    private String id;
    private String name;
    private String title;
    private String description;
    private String validation;
}