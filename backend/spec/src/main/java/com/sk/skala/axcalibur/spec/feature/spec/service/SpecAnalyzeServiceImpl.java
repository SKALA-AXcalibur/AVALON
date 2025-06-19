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
        paths.put("requirement", getPath(project, "REQUIREMENT_FILE"));
        paths.put("interface_def", getPath(project, "INTERFACE_DEFINITION"));
        paths.put("interface_design", getPath(project, "INTERFACE_DESIGN"));
        paths.put("database_design", getPath(project, "DATABASE_DESIGN"));
        return paths;
    }

    /**
     * 프로젝트와 파일 유형명을 기준으로 해당 파일의 경로를 반환합니다.
     * @param project  프로젝트 엔티티
     * @param typeName 파일 유형명 (예: REQUIREMENT_FILE 등)
     * @return 파일 경로 문자열 또는 null
     */
    public String getPath(ProjectEntity project, String typeName) {
        
        FileTypeEntity typeEntity = fileTypeRepository.findByName(typeName)
        .orElseThrow(() -> new BusinessExceptionHandler("파일 유형이 존재하지 않습니다.", ErrorCode.NOT_FOUND_ERROR));

        return filePathRepository.findByProjectKeyAndFileTypeKey(project, typeEntity)
        .orElseThrow(() -> new BusinessExceptionHandler("파일이 존재하지 않습니다.", ErrorCode.NOT_FOUND_ERROR))
        .getPath();
    }
}