package com.sk.skala.axcalibur.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.dto.CreateProjectRequest;
import com.sk.skala.axcalibur.dto.ProjectResponse;
import com.sk.skala.axcalibur.dto.SaveProjectRequest;
import com.sk.skala.axcalibur.dto.SaveProjectResponse;
import com.sk.skala.axcalibur.dto.DeleteProjectResponse;
import com.sk.skala.axcalibur.entity.Project;
import com.sk.skala.axcalibur.entity.Request;
import com.sk.skala.axcalibur.entity.ApiList;
import com.sk.skala.axcalibur.entity.Parameter;
import com.sk.skala.axcalibur.entity.Category;
import com.sk.skala.axcalibur.entity.Context;
import com.sk.skala.axcalibur.repository.ProjectRepository;
import com.sk.skala.axcalibur.repository.RequestRepository;
import com.sk.skala.axcalibur.repository.ApiListRepository;
import com.sk.skala.axcalibur.repository.ParameterRepository;
import com.sk.skala.axcalibur.repository.CategoryRepository;
import com.sk.skala.axcalibur.repository.ContextRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 프로젝트 관리 서비스
// 명세서 기준: 저장, 조회, 삭제, 생성 기능만 제공
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final RequestRepository requestRepository;
    private final ApiListRepository apiListRepository;
    private final ParameterRepository parameterRepository;
    private final CategoryRepository categoryRepository;
    private final ContextRepository contextRepository;

    // 프로젝트 목록 조회 (IF-PR-0002, REQ-PR-F-0002, FUNC-PR-0002)
    public List<ProjectResponse> getAllProjects() {
        log.debug("프로젝트 목록 조회 시작");
        
        List<Project> projects = projectRepository.findAll();
        
        List<ProjectResponse> responses = projects.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
            
        log.debug("프로젝트 목록 조회 완료. 조회된 프로젝트 수: {}", responses.size());
        return responses;
    }

    // 프로젝트 상세 조회 (IF-PR-0002, REQ-PR-F-0002, FUNC-PR-0002)
    public ProjectResponse getProject(String projectId) {
        log.debug("프로젝트 조회 시작. projectId: {}", projectId);
        
        Project project = projectRepository.findByProjectId(projectId).orElse(null);
        
        if (project == null) {
            log.warn("프로젝트를 찾을 수 없습니다. projectId: {}", projectId);
            return null;
        }
            
        ProjectResponse response = convertToResponse(project);
        
        log.debug("프로젝트 조회 완료. projectId: {}", projectId);
        return response;
    }

    // 프로젝트 생성 (IF-PR-0004)
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        log.debug("프로젝트 생성 시작. projectId: {}", request.getProjectId());
        
        // 1. 사용자 입력 ID 사용
        String projectId = request.getProjectId();
        
        // 2. 중복 체크 - 기존 프로젝트가 있으면 기존 것을 반환
        Project existingProject = projectRepository.findByProjectId(projectId).orElse(null);
        
        if (existingProject != null) {
            log.info("기존 프로젝트 발견. 기존 프로젝트 정보 반환. projectId: {}", projectId);
            // 기존 프로젝트에 대한 응답 생성
            String requestTime = LocalDateTime.now().toString();
            String avalon = generateUUID7Cookie();
            return new ProjectResponse(requestTime, projectId, avalon);
        }
        
        // 3. 새 Project 생성
        Project project = new Project();
        project.setProjectId(projectId);
        Project savedProject = projectRepository.save(project);
        
        // 4. CreateProjectRequest는 단순 생성용이므로 추가 데이터 처리 없음
        log.debug("간단 프로젝트 생성 - 추가 데이터 처리 생략");
        
        // 5. 응답 생성 (설계서 기준)
        String requestTime = LocalDateTime.now().toString();
        String avalon = generateUUID7Cookie();
        
        log.info("새 프로젝트 생성 완료. projectId: {}", savedProject.getProjectId());
        return new ProjectResponse(requestTime, savedProject.getProjectId(), avalon);
    }

    // ==================== 설계서 기준 새로운 API 메서드들 ====================
    
    // IF-PR-0001: 프로젝트 목록 저장
    @Transactional
    public SaveProjectResponse saveProject(String projectId, SaveProjectRequest request) {
        log.info("프로젝트 목록 저장 시작. projectId: {}", projectId);
        
        try {
            // 1. 프로젝트 조회 또는 생성
            Project project = projectRepository.findByProjectId(projectId).orElse(null);
            if (project == null) {
                project = new Project();
                project.setProjectId(projectId);
                project = projectRepository.save(project);
                log.info("새 프로젝트 생성. projectId: {}", projectId);
            }
            
            // 2. 요구사항 처리 (확장된 구조)
            if (request.getRequirement() != null && !request.getRequirement().isEmpty()) {
                for (SaveProjectRequest.RequirementItem reqItem : request.getRequirement()) {
                    Request reqEntity = new Request();
                    reqEntity.setName(reqItem.getName());
                    reqEntity.setDescription(reqItem.getDesc());
                    reqEntity.setProject(project);
                    
                    // 확장된 필드들 처리 (향후 분류 테이블과 연계)
                    log.debug("요구사항 확장 정보: priority={}, major={}, middle={}, minor={}", 
                        reqItem.getPriority(), reqItem.getMajor(), reqItem.getMiddle(), reqItem.getMinor());
                    
                    requestRepository.save(reqEntity);
                    log.debug("요구사항 저장: {}", reqItem.getName());
                }
            }
            
            // 3. API 목록 처리 (확장된 구조)
            if (request.getApiList() != null && !request.getApiList().isEmpty()) {
                for (SaveProjectRequest.ApiItem apiItem : request.getApiList()) {
                    ApiList apiEntity = new ApiList();
                    apiEntity.setApiListId(apiItem.getId());
                    apiEntity.setName(apiItem.getName());
                    apiEntity.setDescription(apiItem.getDesc());
                    apiEntity.setMethod(apiItem.getMethod());
                    apiEntity.setUrl(apiItem.getUrl());
                    apiEntity.setPath(apiItem.getPath());
                    apiEntity.setProject(project);
                    
                    ApiList savedApi = apiListRepository.save(apiEntity);
                    
                    // 파라미터 처리 (request/response)
                    processApiParameters(savedApi, apiItem);
                    
                    log.debug("API 저장: {} {} {}", apiItem.getMethod(), apiItem.getUrl(), apiItem.getPath());
                }
            }
            
            String requestTime = LocalDateTime.now().toString();
            SaveProjectResponse response = new SaveProjectResponse(requestTime, "success");
            
            log.info("프로젝트 목록 저장 완료. projectId: {}", projectId);
            return response;
            
        } catch (Exception e) {
            log.error("프로젝트 목록 저장 실패. projectId: {}, 오류: {}", projectId, e.getMessage());
            String requestTime = LocalDateTime.now().toString();
            return new SaveProjectResponse(requestTime, "fail: " + e.getMessage());
        }
    }

    // IF-PR-0002: 프로젝트 목록 조회
    public Object getProjectList() {
        log.info("프로젝트 목록 조회 API 호출");
        
        try {
            // 모든 프로젝트를 상세 정보와 함께 조회
            List<Project> projects = projectRepository.findAll();
            
            List<ProjectResponse> detailedProjects = projects.stream()
                .map(this::convertToDetailedResponse)
                .collect(Collectors.toList());
            
            log.info("프로젝트 목록 조회 성공. 조회된 프로젝트 수: {}", detailedProjects.size());
            return detailedProjects;
            
        } catch (Exception e) {
            log.error("프로젝트 목록 조회 실패. 오류: {}", e.getMessage());
            return "fail: " + e.getMessage();
        }
    }

    // IF-PR-0003: 프로젝트 정보 삭제
    @Transactional
    public DeleteProjectResponse deleteProject(String projectId) {
        log.info("프로젝트 정보 삭제 시작. projectId: {}", projectId);
        
        try {
            Project project = projectRepository.findByProjectId(projectId).orElse(null);
            
            if (project == null) {
                log.warn("삭제할 프로젝트를 찾을 수 없습니다. projectId: {}", projectId);
                String requestTime = LocalDateTime.now().toString();
                return new DeleteProjectResponse(requestTime);
            }
            
            projectRepository.delete(project);
            
            String requestTime = LocalDateTime.now().toString();
            DeleteProjectResponse response = new DeleteProjectResponse(requestTime);
            
            log.info("프로젝트 정보 삭제 완료. projectId: {}", projectId);
            return response;
            
        } catch (Exception e) {
            log.error("프로젝트 정보 삭제 실패. projectId: {}, 오류: {}", projectId, e.getMessage());
            String requestTime = LocalDateTime.now().toString();
            return new DeleteProjectResponse(requestTime);
        }
    }

    // IF-PR-0005: 프로젝트 쿠키 삭제
    public DeleteProjectResponse deleteProjectCookie(String projectId, String avalon) {
        log.info("프로젝트 쿠키 삭제 요청. projectId: {}, avalon: {}", projectId, avalon);
        
        try {
            // 쿠키 삭제는 Controller에서 처리하고, 여기서는 로그만 남김
            log.info("쿠키 삭제 처리 완료. projectId: {}", projectId);
            
            String requestTime = LocalDateTime.now().toString();
            return new DeleteProjectResponse(requestTime);
            
        } catch (Exception e) {
            log.error("쿠키 삭제 처리 실패. projectId: {}, 오류: {}", projectId, e.getMessage());
            String requestTime = LocalDateTime.now().toString();
            return new DeleteProjectResponse(requestTime);
        }
    }

    // UUID7 기반 쿠키 생성 (32초 만료시간용)
    private String generateUUID7Cookie() {
        // UUID7 형식: timestamp(48bit) + random(80bit)
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString();
        return timestamp + "-" + uuid.substring(0, 8);
    }

    // Project → ProjectResponse 변환
    private ProjectResponse convertToResponse(Project project) {
        String requestTime = LocalDateTime.now().toString();
        String avalon = generateUUID7Cookie();
        return new ProjectResponse(requestTime, project.getProjectId(), avalon);
    }

    // Project → 상세 ProjectResponse 변환 (명세서 분석 정보 포함)
    private ProjectResponse convertToDetailedResponse(Project project) {
        String requestTime = LocalDateTime.now().toString();
        String avalon = generateUUID7Cookie();
        
        // 명세서 분석 정보 구성
        String projectName = project.getProjectId() + "_Project";
        List<String> specList = List.of("요구사항명세서", "인터페이스정의서", "인터페이스설계서");
        
        // 요구사항 정보 조회
        List<ProjectResponse.RequirementInfo> requirements = project.getRequirements().stream()
            .map(req -> new ProjectResponse.RequirementInfo(
                req.getName(), 
                req.getDescription(), 
                "중요도미정", "대분류미정", "중분류미정", "소분류미정"))
            .collect(Collectors.toList());
        
        // API 정보 조회  
        List<ProjectResponse.ApiInfo> apiInfos = project.getApiLists().stream()
            .map(api -> {
                ProjectResponse.ApiInfo apiInfo = new ProjectResponse.ApiInfo();
                apiInfo.setId(api.getApiListId());
                apiInfo.setName(api.getName());
                apiInfo.setDesc(api.getDescription());
                apiInfo.setMethod(api.getMethod());
                apiInfo.setUrl(api.getUrl());
                apiInfo.setPath(api.getPath());
                apiInfo.setPathQuery("PathQuery미정");
                
                // 파라미터 정보는 기본값으로 설정 (실제 구현 시 확장)
                ProjectResponse.ParameterGroup request = new ProjectResponse.ParameterGroup();
                ProjectResponse.ParameterGroup response = new ProjectResponse.ParameterGroup();
                apiInfo.setRequest(request);
                apiInfo.setResponse(response);
                
                return apiInfo;
            })
            .collect(Collectors.toList());
        
        return new ProjectResponse(requestTime, project.getProjectId(), avalon, 
                                 projectName, specList, requirements, apiInfos);
    }

    // API 파라미터 처리 (확장된 구조)
    private void processApiParameters(ApiList apiList, SaveProjectRequest.ApiItem apiItem) {
        if (apiItem.getRequest() != null) {
            processParameterGroup(apiList, apiItem.getRequest(), "REQUEST");
        }
        if (apiItem.getResponse() != null) {
            processParameterGroup(apiList, apiItem.getResponse(), "RESPONSE");
        }
    }
    
    // 파라미터 그룹 처리
    private void processParameterGroup(ApiList apiList, SaveProjectRequest.ParameterGroup paramGroup, String groupType) {
        if (paramGroup.getPq() != null) {
            processParameterItems(apiList, paramGroup.getPq(), groupType + "_PQ");
        }
        if (paramGroup.getReq() != null) {
            processParameterItems(apiList, paramGroup.getReq(), groupType + "_REQ");
        }
        if (paramGroup.getRes() != null) {
            processParameterItems(apiList, paramGroup.getRes(), groupType + "_RES");
        }
    }
    
    // 파라미터 아이템들 처리
    private void processParameterItems(ApiList apiList, List<SaveProjectRequest.ParameterItem> paramItems, String paramType) {
        for (SaveProjectRequest.ParameterItem paramItem : paramItems) {
            Parameter parameter = new Parameter();
            parameter.setParameterId(paramItem.getName() + "_" + System.currentTimeMillis());
            parameter.setNameKo(paramItem.getKorName());
            parameter.setName(paramItem.getName());
            parameter.setDataType(paramItem.getDataType());
            parameter.setLength(paramItem.getLength() != null ? Integer.valueOf(paramItem.getLength()) : null);
            parameter.setFormat(paramItem.getFormat());
            parameter.setDefaultValue(paramItem.getDefaultValue());
            parameter.setRequired("Y".equals(paramItem.getRequired()));
            parameter.setDescription(paramItem.getDesc());
            parameter.setApiList(apiList);
            
            // Category와 Context 자동 설정
            setupCategoryAndContext(parameter, paramType);
            
            parameterRepository.save(parameter);
            log.debug("파라미터 저장: {} ({})", paramItem.getName(), paramType);
        }
    }

    // 자동 분류 설정 메서드 (카테고리 + 컨텍스트)
    private void setupCategoryAndContext(Parameter parameter, String parameterType) {
        log.debug("Parameter 자동 분류 설정. type: {}", parameterType);
        
        // Category 설정
        Category category = findOrCreateCategory(parameterType);
        parameter.setCategoryKey(category.getCategoryKey());
        
        // Context 설정  
        Context context = findOrCreateContext(parameterType);
        parameter.setContextKey(context.getContextKey());
    }

    // Category 조회 또는 생성
    private Category findOrCreateCategory(String parameterType) {
        return categoryRepository.findByName(parameterType)
            .orElseGet(() -> {
                Category newCategory = new Category();
                newCategory.setName(parameterType);
                return categoryRepository.save(newCategory);
            });
    }

    // Context 조회 또는 생성
    private Context findOrCreateContext(String parameterType) {
        return contextRepository.findByName(parameterType)
            .orElseGet(() -> {
                Context newContext = new Context();
                newContext.setName(parameterType);
                return contextRepository.save(newContext);
            });
    }
}