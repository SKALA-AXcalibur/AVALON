package com.sk.skala.axcalibur.feature.service;

import com.sk.skala.axcalibur.feature.dto.CreateProjectRequest;
import com.sk.skala.axcalibur.feature.dto.CreateProjectResponse;
import com.sk.skala.axcalibur.feature.dto.DeleteProjectResponse;
import com.sk.skala.axcalibur.feature.dto.ProjectResponse;
import com.sk.skala.axcalibur.feature.dto.SaveProjectRequest;
import com.sk.skala.axcalibur.feature.dto.SaveProjectResponse;

/**
 * 프로젝트 관리 서비스 인터페이스
 * 명세서 기준: 저장, 조회, 삭제, 생성 기능만 제공
 */
public interface ProjectService {
    
    //  프로젝트 목록 저장 (IF-PR-0001)
    SaveProjectResponse saveProject(String projectId, SaveProjectRequest request);

    // 프로젝트 상세 조회 (IF-PR-0002)
    ProjectResponse getProjectDetails(String avalon);

    // 프로젝트 정보 삭제 (IF-PR-0003)
    DeleteProjectResponse deleteProject(String projectId);
    
    // 프로젝트 생성 (IF-PR-0004)
    CreateProjectResponse createProject(CreateProjectRequest request);

    // 프로젝트 쿠키 삭제 (IF-PR-0005)
    DeleteProjectResponse deleteProjectCookie(String avalon);
}