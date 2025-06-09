package com.sk.skala.axcalibur.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sk.skala.axcalibur.dto.CreateProjectRequest;
import com.sk.skala.axcalibur.dto.ProjectResponse;
import com.sk.skala.axcalibur.dto.SaveProjectRequest;
import com.sk.skala.axcalibur.dto.SaveProjectResponse;
import com.sk.skala.axcalibur.dto.DeleteProjectResponse;
import com.sk.skala.axcalibur.service.ProjectService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/project/v1")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    // ==================== 설계서 기준 5개 API ====================

    /**
     * IF-PR-0001: 프로젝트 목록 저장
     * 인터페이스명: 프로젝트 목록 저장 
     * 설명: 명세서 분석 결과를 프로젝트에 저장
     * URL: POST /api/project/v1/{projectId}
     */
    @PostMapping("/{projectId}")
    public ResponseEntity<SaveProjectResponse> saveProject(
            @PathVariable String projectId,
            @RequestBody SaveProjectRequest request,
            @CookieValue(name = "avalon", required = false) String avalon,
            HttpServletResponse httpResponse) {
        
        log.info("[프로젝트 목록 저장] 요청. projectId: {}", projectId);
        
        try {
            SaveProjectResponse response = projectService.saveProject(projectId, request);
            
            // avalon 쿠키 설정 (32초)
            if (avalon != null) {
                Cookie avalonCookie = new Cookie("avalon", avalon);
                avalonCookie.setMaxAge(32);
                avalonCookie.setPath("/");
                httpResponse.addCookie(avalonCookie);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("[프로젝트 목록 저장] 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * IF-PR-0002: 프로젝트 목록 조회
     * 인터페이스명: 프로젝트 정보 조회 
     * 설명: 프로젝트 인증값을 이용해서 명세서 분석 정보를 조회
     * URL: GET /api/project/v1
     */
    @GetMapping("")
    public ResponseEntity<Object> getProjectList(
            @CookieValue(name = "avalon", required = false) String avalon) {
        
        log.info("[프로젝트 정보 조회] 요청. avalon: {}", avalon);
        
        try {
            Object response = projectService.getProjectList();
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("[프로젝트 정보 조회] 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * IF-PR-0003: 프로젝트 정보 삭제
     * 인터페이스명: 프로젝트 정보 삭제
     * 설명: 특정 프로젝트 정보를 삭제
     * URL: DELETE /api/project/v1/{projectId}
     */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<DeleteProjectResponse> deleteProject(@PathVariable String projectId) {
        log.info("[프로젝트 정보 삭제] 요청. projectId: {}", projectId);
        
        try {
            DeleteProjectResponse response = projectService.deleteProject(projectId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("[프로젝트 정보 삭제] 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * IF-PR-0004: 프로젝트 생성
     * 인터페이스명: 프로젝트 생성
     * 설명: 새로운 프로젝트를 생성하고 인증 쿠키 발급
     * URL: POST /api/project/v1
     */
    @PostMapping("")
    public ResponseEntity<ProjectResponse> createProject(
            @RequestBody CreateProjectRequest request, 
            HttpServletResponse httpResponse) {
        
        log.info("[프로젝트 생성] 요청. projectId: {}", request.getProjectId());
        
        try {
            ProjectResponse response = projectService.createProject(request);
            
            // avalon 쿠키 설정 (32초)
            Cookie avalon = new Cookie("avalon", response.getAvalon());
            avalon.setMaxAge(32);
            avalon.setPath("/");
            httpResponse.addCookie(avalon);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("[프로젝트 생성] 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * IF-PR-0005: 프로젝트 쿠키 삭제
     * 인터페이스명: 프로젝트 쿠키 삭제
     * 설명: 삭제할 프로젝트 쿠키. setMaxAge를 0으로 설정해서 응답
     * URL: DELETE /api/project/v1
     */
    @DeleteMapping("")
    public ResponseEntity<DeleteProjectResponse> deleteProjectCookie(
            @RequestParam String projectId,
            @CookieValue(name = "avalon", required = false) String avalon,
            HttpServletResponse httpResponse) {
        
        log.info("[프로젝트 쿠키 삭제] 요청. projectId: {}, avalon: {}", projectId, avalon);
        
        try {
            DeleteProjectResponse response = projectService.deleteProjectCookie(projectId, avalon);
            
            // 쿠키 삭제: setMaxAge(0)으로 만료 처리
            if (avalon != null) {
                Cookie avalonCookie = new Cookie("avalon", "");
                avalonCookie.setMaxAge(0);  // 쿠키 즉시 만료
                avalonCookie.setPath("/");
                httpResponse.addCookie(avalonCookie);
                log.info("[프로젝트 쿠키 삭제] avalon 쿠키 만료 처리 완료");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("[프로젝트 쿠키 삭제] 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== 개발용 API ====================

    /**
     * Health Check API (개발용)
     * 설명: 서버 상태 확인
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.debug("Health check 요청");
        return ResponseEntity.ok("AXCalibur API Server is running!");
    }

    /**
     * 간단한 테스트 API (개발용)
     * 설명: API 연결 테스트
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test API is working!");
    }
}