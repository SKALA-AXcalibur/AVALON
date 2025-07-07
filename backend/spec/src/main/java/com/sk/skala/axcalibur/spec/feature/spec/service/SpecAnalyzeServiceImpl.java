package com.sk.skala.axcalibur.spec.feature.spec.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.spec.global.entity.FileTypeEntity;
import com.sk.skala.axcalibur.spec.global.entity.ProjectEntity;
import com.sk.skala.axcalibur.spec.global.repository.FileTypeRepository;
import com.sk.skala.axcalibur.spec.global.repository.FilePathRepository;
import com.sk.skala.axcalibur.spec.global.code.ErrorCode;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.spec.feature.spec.enums.FileTypeName;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class SpecAnalyzeServiceImpl implements SpecAnalyzeService {

    private final FilePathRepository filePathRepository;
    private final FileTypeRepository fileTypeRepository;

    /**
     * 주어진 프로젝트에 대한 모든 명세 파일 경로를 분석하여 반환합니다.
     * @param project 분석 대상 프로젝트 엔티티
     * @return 파일 유형별 경로 맵
     */
    public Map<String, String> analyze(ProjectEntity project) {
        Map<String, String> paths = new HashMap<>();
        paths.put("requirement", getPath(project, FileTypeName.REQUIREMENT_FILE));
        paths.put("interface_def", getPath(project, FileTypeName.INTERFACE_DEFINITION));
        paths.put("interface_design", getPath(project, FileTypeName.INTERFACE_DESIGN));
        paths.put("database_design", getPath(project, FileTypeName.DATABASE_DESIGN));
        return paths;
    }

    /**
     * 프로젝트와 파일 유형명을 기준으로 해당 파일의 경로를 반환합니다.
     * @param project  프로젝트 엔티티
     * @param typeName 파일 유형명 (예: REQUIREMENT_FILE 등)
     */
    public String getPath(ProjectEntity project, FileTypeName typeName) {
        
        FileTypeEntity typeEntity = fileTypeRepository.findByName(typeName.name())
        .orElseThrow(() -> new BusinessExceptionHandler(String.format("파일 유형 '%s'이(가) 존재하지 않습니다.", typeName), ErrorCode.NOT_FOUND_ERROR));

        return filePathRepository.findByProjectKeyAndFileTypeKey(project, typeEntity)
        .orElseThrow(() -> new BusinessExceptionHandler(String.format("프로젝트 '%s'에 대한 파일 유형 '%s'의 파일 경로를 찾을 수 없습니다.", project.getId(), typeEntity.getName()), ErrorCode.NOT_FOUND_ERROR))
        .getPath();
    }
}