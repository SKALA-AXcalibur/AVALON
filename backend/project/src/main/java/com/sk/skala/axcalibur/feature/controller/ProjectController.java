package com.sk.skala.axcalibur.feature.controller;

import com.sk.skala.axcalibur.feature.dto.CreateProjectRequest;
import com.sk.skala.axcalibur.feature.dto.CreateProjectResponse;
import com.sk.skala.axcalibur.feature.dto.DeleteProjectResponse;
import com.sk.skala.axcalibur.feature.dto.ProjectResponse;
import com.sk.skala.axcalibur.feature.dto.SaveProjectRequest;
import com.sk.skala.axcalibur.feature.dto.SaveProjectResponse;
import com.sk.skala.axcalibur.feature.service.ProjectServiceImpl;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/project/v1")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectServiceImpl projectService;

    // URL: POST /api/project/v1/{projectId}
    @PostMapping("/{projectId}")
    public ResponseEntity<SaveProjectResponse> saveProject(
            @PathVariable String projectId,
            @RequestBody SaveProjectRequest request,
            @CookieValue(name = "Cookie") String avalon) {

        log.info("[프로젝트 목록 저장] 요청. projectId: {}", projectId);

        SaveProjectResponse response = projectService.saveProject(projectId, request);

        // Spring의 ResponseCookie를 사용하여 쿠키 갱신 (보안 강화)
        ResponseCookie sessionCookie = ResponseCookie.from("Cookie", avalon)
                .path("/")
                .maxAge(1800)       // 쿠키 유효기간 30분으로 갱신
                .httpOnly(true)     // Javascript에서 접근 불가 (XSS 방지)
                .secure(true)       // HTTPS 환경에서만 쿠키 전송
                .build();

        return ResponseEntity
                .status(SuccessCode.UPDATE_SUCCESS.getStatus())
                .header(HttpHeaders.SET_COOKIE, sessionCookie.toString())
                .body(response);
    }

    // URL: GET /api/project/v1
    @GetMapping("")
    public ResponseEntity<ProjectResponse> getProjectDetails(
            @CookieValue(name = "Cookie") String avalon) {

        log.info("[프로젝트 정보 조회] 요청. avalon: '{}'", avalon);
        ProjectResponse response = projectService.getProjectDetails(avalon);

        if (response == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .status(SuccessCode.SELECT_SUCCESS.getStatus())
                .header("X-Request-Time", LocalDateTime.now().toString())
                .body(response);
    }

    // URL: DELETE /api/project/v1/{projectId}
    @DeleteMapping("/{projectId}")
    public ResponseEntity<DeleteProjectResponse> deleteProject(@PathVariable String projectId) {
        log.info("[프로젝트 정보 삭제] 요청. projectId: {}", projectId);
        DeleteProjectResponse response = projectService.deleteProject(projectId);
        return ResponseEntity.status(SuccessCode.DELETE_SUCCESS.getStatus()).body(response);
    }

    /**
     * IF-PR-0004: 프로젝트 생성
     * 인터페이스명: 프로젝트 생성
     * 설명: 새로운 프로젝트를 생성하고 인증 쿠키 발급
     * URL: POST /api/project/v1
     */
    @PostMapping("")
    public ResponseEntity<CreateProjectResponse> createProject(
            @RequestBody CreateProjectRequest request) {

        log.info("[프로젝트 생성] 요청. projectId: {}", request.getProjectId());
        CreateProjectResponse response = projectService.createProject(request);

        // Spring의 ResponseCookie를 사용하여 쿠키 생성 (보안 강화)
        ResponseCookie sessionCookie = ResponseCookie.from("Cookie", response.getAvalon())
                .path("/")
                .maxAge(1800)       // 30분
                .httpOnly(true)
                .secure(true)
                .build();

        return ResponseEntity
                .status(SuccessCode.INSERT_SUCCESS.getStatus())
                .header(HttpHeaders.SET_COOKIE, sessionCookie.toString())
                .body(response);
    }

    // URL: DELETE /api/project/v1
    @DeleteMapping("")
    public ResponseEntity<DeleteProjectResponse> deleteProjectCookie(
            @CookieValue(name = "Cookie") String avalon) {

        log.info("[프로젝트 쿠키 삭제] 요청. avalon: {}", avalon);
        DeleteProjectResponse response = projectService.deleteProjectCookie(avalon);

        // Spring의 ResponseCookie를 사용하여 쿠키 삭제
        ResponseCookie expiringCookie = ResponseCookie.from("Cookie", "")
                .path("/")
                .maxAge(0)       // 쿠키 즉시 만료
                .build();

        log.info("[프로젝트 쿠키 삭제] Cookie 쿠키 만료 처리 완료");

        return ResponseEntity
                .status(SuccessCode.DELETE_SUCCESS.getStatus())
                .header(HttpHeaders.SET_COOKIE, expiringCookie.toString())
                .header("X-Request-Time", LocalDateTime.now().toString())
                .body(response);
    }
}