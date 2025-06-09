package com.sk.skala.axcalibur.spec.feature.spec.code;

import lombok.Getter;

/**
 * 3가지의 요구사항 문서 분류를 위한 코드 정의
 * REQUIREMENT_FILE : 요구사항정의서
 * INTERFACE_DEFINITION : 인터페이스정의서
 * INTERFACE_DESIGN : 인터페이스설계서
 */
@Getter
public enum FileType {
    REQUIREMENT_FILE(1),
    INTERFACE_DEFINITION(2),
    INTERFACE_DESIGN(3);

    private final int typeKey;

    FileType(int typeKey) {
        this.typeKey = typeKey;
    }
}