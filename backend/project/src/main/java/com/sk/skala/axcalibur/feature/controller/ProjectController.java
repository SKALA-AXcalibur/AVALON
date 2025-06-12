package com.sk.skala.axcalibur.feature.controller;

import com.sk.skala.axcalibur.feature.dto.CreateProjectRequestDto;
import com.sk.skala.axcalibur.feature.dto.CreateProjectResponseDto;
import com.sk.skala.axcalibur.feature.dto.DeleteProjectCookieDto;
import com.sk.skala.axcalibur.feature.dto.DeleteProjectResponseDto;
import com.sk.skala.axcalibur.feature.dto.ProjectResponseDto;
import com.sk.skala.axcalibur.feature.dto.SaveProjectRequestDto;
import com.sk.skala.axcalibur.feature.dto.SaveProjectResponseDto;
import com.sk.skala.axcalibur.feature.service.ProjectServiceImpl;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/project/v1")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectServiceImpl projectService;

    /**
     * IF-PR-0001: 프로젝트 정보 저장
     * 인터페이스명: 프로젝트 정보 저장
     * 설명: 프로젝트 정보 저장
     * URL: POST /api/project/v1/{projectId}
     */
    @PostMapping("/{projectId}")
    public ResponseEntity<SuccessResponse<SaveProjectResponseDto>> saveProject(
            @PathVariable("projectId") String projectId,
            @RequestBody SaveProjectRequestDto request,
            @CookieValue(name = "Avalon") String avalon) {

        log.info("[프로젝트 정보 저장] 요청. projectId: {}", projectId);

        SaveProjectResponseDto response = projectService.saveProject(projectId, request);

        // Spring의 ResponseCookie를 사용하여 쿠키 갱신 (보안 강화)
        ResponseCookie sessionCookie = ResponseCookie.from("Avalon", avalon)
                .path("/")
                .maxAge(86400)       // 쿠키 유효기간 1일로 갱신
                .httpOnly(true)     // Javascript에서 접근 불가 (XSS 방지)
                .secure(true)       // HTTPS 환경에서만 쿠키 전송
                .build();
        return ResponseEntity
                .status(SuccessCode.INSERT_SUCCESS.getStatus())
                .header(HttpHeaders.SET_COOKIE, sessionCookie.toString())
                .body(new SuccessResponse<>(response, SuccessCode.INSERT_SUCCESS, "프로젝트 정보 저장 성공"));
    }

    /**
     * IF-PR-0002: 프로젝트 정보 조회
     * 인터페이스명: 프로젝트 정보 조회
     * 설명: 프로젝트 정보 조회
     * URL: GET /api/project/v1
     */
    @GetMapping("")
    public ResponseEntity<SuccessResponse<ProjectResponseDto>> getProjectDetails(
            @CookieValue(name = "Avalon") String avalon) {
    
        log.info("[프로젝트 정보 조회] 요청. avalon: '{}'", avalon);
        
        ProjectResponseDto response = projectService.getProjectDetails(avalon);
    
        //  응답 시간 설정
        String responseTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    
        return ResponseEntity
                .status(SuccessCode.SELECT_SUCCESS.getStatus())
                .header("responseTime", responseTime)
                .body(new SuccessResponse<>(response, SuccessCode.SELECT_SUCCESS, "프로젝트 정보 조회 성공"));
    }

    // URL: DELETE /api/project/v1/{projectId}
    @DeleteMapping("/{projectId}")
    public ResponseEntity<SuccessResponse<DeleteProjectResponseDto>> deleteProject(@PathVariable("projectId") String projectId) {
        log.info("[프로젝트 정보 삭제] 요청. projectId: {}", projectId);
        DeleteProjectResponseDto response = projectService.deleteProject(projectId);

        //  응답 시간 설정
        String requestTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        return ResponseEntity
                .status(SuccessCode.DELETE_SUCCESS.getStatus())
                .header("requestTime", requestTime)
                .body(new SuccessResponse<>(response, SuccessCode.DELETE_SUCCESS, "프로젝트 정보 삭제 성공"));
    }

    /**
     * IF-PR-0004: 프로젝트 생성
     * 인터페이스명: 프로젝트 생성
     * 설명: 새로운 프로젝트를 생성하고 인증 쿠키 발급
     * URL: POST /api/project/v1
     */
    @PostMapping("")
    public ResponseEntity<SuccessResponse<CreateProjectResponseDto>> createProject(
            @RequestBody CreateProjectRequestDto request) {

        log.info("[프로젝트 생성] 요청. projectId: {}", request.getProjectId());
        CreateProjectResponseDto response = projectService.createProject(request);

        // Spring의 ResponseCookie를 사용하여 쿠키 생성 (보안 강화)
        ResponseCookie sessionCookie = ResponseCookie.from("Avalon", response.getAvalon())
                .path("/")
                .maxAge(86400)       // 1일
                .httpOnly(true)
                .secure(true)
                .build();

        //  응답 시간 설정
        String responseTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        return ResponseEntity
                .status(SuccessCode.INSERT_SUCCESS.getStatus())
                .header(HttpHeaders.SET_COOKIE, sessionCookie.toString())
                .header("responseTime", responseTime)
                .body(new SuccessResponse<>(response, SuccessCode.INSERT_SUCCESS, "프로젝트 생성 성공"));
    }

    /**
     * IF-PR-0005: 프로젝트 쿠키 삭제
     * 인터페이스명: 프로젝트 쿠키 삭제
     * 설명: 프로젝트 쿠키 삭제
     * URL: DELETE /api/project/v1
     */
    @DeleteMapping("")
    public ResponseEntity<SuccessResponse<DeleteProjectCookieDto>> deleteProjectCookie(
            @CookieValue(name = "Avalon") String avalon) {

        log.info("[프로젝트 쿠키 삭제] 요청. avalon: {}", avalon);
        DeleteProjectCookieDto response = projectService.deleteProjectCookie(avalon);

        // Spring의 ResponseCookie를 사용하여 쿠키 삭제
        ResponseCookie expiringCookie = ResponseCookie.from("Avalon", "")
                .path("/")
                .maxAge(0)       // 쿠키 즉시 만료
                .build();

        //  응답 시간 설정
        String requestTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        return ResponseEntity
                .status(SuccessCode.DELETE_SUCCESS.getStatus())
                .header(HttpHeaders.SET_COOKIE, expiringCookie.toString())
                .header("requestTime", requestTime)
                .body(new SuccessResponse<>(response, SuccessCode.DELETE_SUCCESS, "프로젝트 쿠키 삭제 성공"));
                
    }
}