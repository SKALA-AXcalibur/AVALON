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


    public Map<String, String> analyze(ProjectEntity project) {
        Map<String, String> paths = new HashMap<>();
        paths.put("requirement", getPath(project, "REQUIREMENT_FILE"));
        paths.put("interfaceDef", getPath(project, "INTERFACE_DEFINITION"));
        paths.put("interfaceDesign", getPath(project, "INTERFACE_DESIGN"));
        paths.put("databaseDesign", getPath(project, "DATABASE_DESIGN"));
        return paths;
    }


    public String getPath(ProjectEntity project, String typeName) {

        FileTypeEntity typeEntity = fileTypeRepository.findByName(typeName)
        .orElseThrow(() -> new BusinessExceptionHandler("파일 유형이 존재하지 않습니다.", ErrorCode.NOT_FOUND_ERROR));

       
        return filePathRepository.findByProjectKeyAndFileTypeKey(project, typeEntity)
        .orElseThrow(() -> new BusinessExceptionHandler("파일이 존재하지 않습니다.", ErrorCode.NOT_FOUND_ERROR))
        .getPath();
    }
}