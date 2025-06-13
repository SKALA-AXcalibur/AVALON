package com.sk.skala.axcalibur.feature.controller;

import com.sk.skala.axcalibur.feature.dto.CreateProjectRequestDto;
import com.sk.skala.axcalibur.feature.dto.CreateProjectResponseDto;
import com.sk.skala.axcalibur.feature.dto.DeleteProjectCookieDto;
import com.sk.skala.axcalibur.feature.dto.DeleteProjectResponseDto;
import com.sk.skala.axcalibur.feature.dto.ProjectResponseDto;
import com.sk.skala.axcalibur.feature.dto.SaveProjectRequestDto;
import com.sk.skala.axcalibur.feature.dto.SaveProjectResponseDto;
import com.sk.skala.axcalibur.feature.service.ProjectService;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 프로젝트 정보 관리 컨트롤러
 * 
 * 프로젝트 정보 저장, 조회, 삭제, 생성, 쿠키 삭제 기능 제공
 * 
 * 
 */

@RestController
@RequestMapping("/project/v1")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    /**
     * IF-PR-0001: 프로젝트 정보 저장
     * 인터페이스명: 프로젝트 정보 저장
     * 설명: 프로젝트 정보 저장
     * URL: POST /api/project/v1/{projectId}
     *
     * @param projectId 저장할 프로젝트의 고유 식별자
     * @param request 저장할 프로젝트 정보를 담은 요청 DTO
     * @return 프로젝트 저장 결과를 담은 응답 객체
     */
    @PostMapping("/{projectId}")
    public ResponseEntity<SuccessResponse<SaveProjectResponseDto>> saveProject(
            @PathVariable("projectId") String projectId,
            @Valid @RequestBody SaveProjectRequestDto request) {

        log.info("[프로젝트 정보 저장] 요청. projectId: {}", projectId);

        SaveProjectResponseDto response = projectService.saveProject(projectId, request);
        return ResponseEntity
                .status(SuccessCode.INSERT_SUCCESS.getStatus())
                .body(new SuccessResponse<>(response, SuccessCode.INSERT_SUCCESS, "프로젝트 정보 저장 성공"));
    }

    /**
     * IF-PR-0002: 프로젝트 정보 조회
     * 인터페이스명: 프로젝트 정보 조회
     * 설명: 프로젝트 정보 조회
     * URL: GET /api/project/v1
     *
     * @param avalon 프로젝트 인증을 위한 쿠키 값
     * @return 프로젝트 상세 정보를 담은 응답 객체
     */
    @GetMapping("")
    public ResponseEntity<SuccessResponse<ProjectResponseDto>> getProjectDetails(
            @CookieValue(name = "avalon") String avalon) {
    
        log.info("[프로젝트 정보 조회] 요청.");
        
        ProjectResponseDto response = projectService.getProjectDetails(avalon);
    
        //  응답 시간 설정
        String responseTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    
        return ResponseEntity
                .status(SuccessCode.SELECT_SUCCESS.getStatus())
                .header("responseTime", responseTime)
                .body(new SuccessResponse<>(response, SuccessCode.SELECT_SUCCESS, "프로젝트 정보 조회 성공"));
    }

    /**
     * IF-PR-0003: 프로젝트 정보 삭제
     * 인터페이스명: 프로젝트 정보 삭제
     * 설명: 프로젝트 정보 삭제
     * URL: DELETE /api/project/v1/{projectId}
     *
     * @param projectId 삭제할 프로젝트의 고유 식별자
     * @return 프로젝트 삭제 결과를 담은 응답 객체
     * @throws IOException 파일 시스템 작업 중 오류 발생 시
     */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<SuccessResponse<DeleteProjectResponseDto>> deleteProject(@PathVariable("projectId") String projectId) throws IOException {
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
     *
     * @param request 프로젝트 생성에 필요한 정보를 담은 요청 DTO
     * @return 프로젝트 생성 결과와 인증 쿠키를 담은 응답 객체
     */
    @PostMapping("")
    public ResponseEntity<SuccessResponse<CreateProjectResponseDto>> createProject(
            @RequestBody CreateProjectRequestDto request) {

        log.info("[프로젝트 생성] 요청. projectId: {}", request.getProjectId());
        CreateProjectResponseDto response = projectService.createProject(request);

        //  응답 시간 설정
        String responseTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        return ResponseEntity
                .status(SuccessCode.INSERT_SUCCESS.getStatus())
                .header("responseTime", responseTime)
                .body(new SuccessResponse<>(response, SuccessCode.INSERT_SUCCESS, "프로젝트 생성 성공"));
    }

    /**
     * IF-PR-0005: 프로젝트 쿠키 삭제
     * 인터페이스명: 프로젝트 쿠키 삭제
     * 설명: 프로젝트 쿠키 삭제
     * URL: DELETE /api/project/v1
     *
     * @param avalon 삭제할 프로젝트 인증 쿠키 값
     * @return 프로젝트 쿠키 삭제 결과를 담은 응답 객체
     */
    @DeleteMapping("")
    public ResponseEntity<SuccessResponse<DeleteProjectCookieDto>> deleteProjectCookie(
            @CookieValue(name = "avalon") String avalon) {

        log.info("[프로젝트 쿠키 삭제] 요청. avalon: {}", avalon);
        DeleteProjectCookieDto response = projectService.deleteProjectCookie(avalon);

        // Spring의 ResponseCookie를 사용하여 쿠키 삭제
        ResponseCookie expiringCookie = ResponseCookie.from("avalon", "")
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