package com.sk.skala.axcalibur.feature.service;

import com.sk.skala.axcalibur.feature.dto.CreateProjectRequestDto;
import com.sk.skala.axcalibur.feature.dto.CreateProjectResponseDto;
import com.sk.skala.axcalibur.feature.dto.DeleteProjectCookieDto;
import com.sk.skala.axcalibur.feature.dto.DeleteProjectResponseDto;
import com.sk.skala.axcalibur.feature.dto.ProjectResponseDto;
import com.sk.skala.axcalibur.feature.dto.SaveProjectRequestDto;
import com.sk.skala.axcalibur.feature.dto.SaveProjectResponseDto;

/**
 * 프로젝트 관리 서비스 인터페이스
 * 명세서 기준: 저장, 조회, 삭제, 생성 기능만 제공
 */
public interface ProjectService {
    
    //  프로젝트 목록 저장 (IF-PR-0001)
    SaveProjectResponseDto saveProject(String projectId, SaveProjectRequestDto request, String avalon);

    // 프로젝트 상세 조회 (IF-PR-0002)
    ProjectResponseDto getProjectDetails(String avalon);

    // 프로젝트 정보 삭제 (IF-PR-0003)
    DeleteProjectResponseDto deleteProject(String projectId);
    
    // 프로젝트 생성 (IF-PR-0004)
    CreateProjectResponseDto createProject(CreateProjectRequestDto request);

    // 프로젝트 쿠키 삭제 (IF-PR-0005)
    DeleteProjectCookieDto deleteProjectCookie(String avalon);
}