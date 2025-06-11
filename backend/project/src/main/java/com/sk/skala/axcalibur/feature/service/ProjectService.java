package com.sk.skala.axcalibur.feature.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.axcalibur.feature.dto.CreateProjectRequestDTO;
import com.sk.skala.axcalibur.feature.dto.CreateProjectResponseDTO;
import com.sk.skala.axcalibur.feature.dto.DeleteProjectResponseDTO;
import com.sk.skala.axcalibur.feature.dto.ProjectResponseDTO;
import com.sk.skala.axcalibur.feature.dto.SaveProjectRequestDTO;
import com.sk.skala.axcalibur.feature.dto.SaveProjectResponseDTO;
import com.sk.skala.axcalibur.feature.entity.ProjectEntity;

/**
 * 프로젝트 관리 서비스 인터페이스
 * 명세서 기준: 저장, 조회, 삭제, 생성 기능만 제공
 */
public interface ProjectService extends JpaRepository<ProjectEntity, String>{
    
    //  프로젝트 목록 저장 (IF-PR-0001)
    SaveProjectResponseDTO saveProject(String projectId, SaveProjectRequestDTO request);

    // 프로젝트 상세 조회 (IF-PR-0002)
    ProjectResponseDTO getProjectDetails(String avalon);

    // 프로젝트 정보 삭제 (IF-PR-0003)
    DeleteProjectResponseDTO deleteProject(String projectId);
    
    // 프로젝트 생성 (IF-PR-0004)
    CreateProjectResponseDTO createProject(CreateProjectRequestDTO request);

    // 프로젝트 쿠키 삭제 (IF-PR-0005)
    DeleteProjectResponseDTO deleteProjectCookie(String avalon);
}