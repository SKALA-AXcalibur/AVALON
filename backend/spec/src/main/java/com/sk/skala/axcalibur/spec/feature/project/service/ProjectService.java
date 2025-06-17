package com.sk.skala.axcalibur.spec.feature.project.service;

import com.sk.skala.axcalibur.spec.feature.project.dto.CreateProjectRequestDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.CreateProjectResponseDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.DeleteProjectCookieDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.DeleteProjectResponseDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.ProjectResponseDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.SaveProjectRequestDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.SaveProjectResponseDto;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;

/**
 * 프로젝트 관리 서비스 인터페이스
 * 명세서 기준: 저장, 조회, 삭제, 생성 기능만 제공
 */
public interface ProjectService {
    
    /**
     * 프로젝트 목록 저장
     * @param projectId 프로젝트 고유 식별자
     * @param request 저장할 프로젝트 정보
     * @return 저장된 프로젝트 정보
     */
    SaveProjectResponseDto saveProject(String projectId, SaveProjectRequestDto request);

    /**
     * 프로젝트 상세 조회
     * @param avalon 프로젝트 고유 식별자
     * @return 프로젝트 상세 정보
     */
    ProjectResponseDto getProjectDetails(String avalon);

    /**
     * 프로젝트 정보를 삭제합니다.
     *
     * @param projectId 삭제할 프로젝트의 고유 식별자
     * @return 프로젝트 삭제 결과 DTO
     * @throws BusinessExceptionHandler 프로젝트를 찾을 수 없거나 파일 삭제 중 오류가 발생한 경우
     */
    DeleteProjectResponseDto deleteProject(String projectId);
    
    /**
     * 프로젝트 생성
     * @param request 생성할 프로젝트 정보
     * @return 생성된 프로젝트 정보
     */
    CreateProjectResponseDto createProject(CreateProjectRequestDto request);

    /**
     * 프로젝트 쿠키 삭제
     * @param avalon 프로젝트 고유 식별자
     * @return 프로젝트 쿠키 삭제 결과 DTO
     */
    DeleteProjectCookieDto deleteProjectCookie(String avalon);
}