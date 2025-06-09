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
import com.sk.skala.axcalibur.entity.Priority;
import com.sk.skala.axcalibur.entity.RequestMajor;
import com.sk.skala.axcalibur.entity.RequestMiddle;
import com.sk.skala.axcalibur.entity.RequestMinor;
import com.sk.skala.axcalibur.repository.ProjectRepository;
import com.sk.skala.axcalibur.repository.RequestRepository;
import com.sk.skala.axcalibur.repository.ApiListRepository;
import com.sk.skala.axcalibur.repository.ParameterRepository;
import com.sk.skala.axcalibur.repository.CategoryRepository;
import com.sk.skala.axcalibur.repository.ContextRepository;
import com.sk.skala.axcalibur.repository.PriorityRepository;
import com.sk.skala.axcalibur.repository.RequestMajorRepository;
import com.sk.skala.axcalibur.repository.RequestMiddleRepository;
import com.sk.skala.axcalibur.repository.RequestMinorRepository;

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
    private final PriorityRepository priorityRepository;
    private final RequestMajorRepository requestMajorRepository;
    private final RequestMiddleRepository requestMiddleRepository;
    private final RequestMinorRepository requestMinorRepository;

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
                    
                    // 분류 정보를 실제 DB에 저장
                    processRequirementClassification(reqEntity, reqItem);
                    
                    requestRepository.save(reqEntity);
                    log.debug("요구사항 저장: {} (priority={}, major={}, middle={}, minor={})", 
                        reqItem.getName(), reqItem.getPriority(), reqItem.getMajor(), 
                        reqItem.getMiddle(), reqItem.getMinor());
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
            .map(req -> {
                // 실제 분류 정보 조회
                String priority = req.getPriorityKey() != null && req.getPriority() != null ? 
                    req.getPriority().getName() : "중요도미정";
                String major = req.getMajorKey() != null && req.getRequestMajor() != null ? 
                    req.getRequestMajor().getName() : "대분류미정";
                String middle = req.getMiddleKey() != null && req.getRequestMiddle() != null ? 
                    req.getRequestMiddle().getName() : "중분류미정";
                String minor = req.getMinorKey() != null && req.getRequestMinor() != null ? 
                    req.getRequestMinor().getName() : "소분류미정";
                    
                return new ProjectResponse.RequirementInfo(
                    req.getName(), 
                    req.getDescription(), 
                    priority, major, middle, minor);
            })
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
                
                // PathQuery 구성 (URL + Path 조합 또는 실제 저장된 정보)
                String pathQuery = api.getUrl() != null && api.getPath() != null ? 
                    api.getUrl() + api.getPath() : "PathQuery정보없음";
                apiInfo.setPathQuery(pathQuery);
                
                // 실제 저장된 파라미터 정보로 request/response 구성
                ProjectResponse.ParameterGroup requestGroup = buildParameterGroup(api, "REQUEST");
                ProjectResponse.ParameterGroup responseGroup = buildParameterGroup(api, "RESPONSE");
                apiInfo.setRequest(requestGroup);
                apiInfo.setResponse(responseGroup);
                
                return apiInfo;
            })
            .collect(Collectors.toList());
        
        return new ProjectResponse(requestTime, project.getProjectId(), avalon, 
                                 projectName, specList, requirements, apiInfos);
    }

    // API의 파라미터 그룹 구성 (REQUEST 또는 RESPONSE)
    private ProjectResponse.ParameterGroup buildParameterGroup(ApiList api, String groupType) {
        ProjectResponse.ParameterGroup paramGroup = new ProjectResponse.ParameterGroup();
        
        // API에 연결된 모든 파라미터를 조회
        List<Parameter> parameters = api.getParameters();
        if (parameters == null || parameters.isEmpty()) {
            return paramGroup; // 빈 그룹 반환
        }
        
        // groupType에 따라 파라미터 필터링 및 분류
        List<ProjectResponse.ParameterDetail> pqParams = parameters.stream()
            .filter(p -> isParameterType(p, groupType + "_PQ"))
            .map(this::convertToParameterDetail)
            .collect(Collectors.toList());
            
        List<ProjectResponse.ParameterDetail> reqParams = parameters.stream()
            .filter(p -> isParameterType(p, groupType + "_REQ"))
            .map(this::convertToParameterDetail)
            .collect(Collectors.toList());
            
        List<ProjectResponse.ParameterDetail> resParams = parameters.stream()
            .filter(p -> isParameterType(p, groupType + "_RES"))
            .map(this::convertToParameterDetail)
            .collect(Collectors.toList());
        
        paramGroup.setPq(pqParams);
        paramGroup.setReq(reqParams);
        paramGroup.setRes(resParams);
        
        return paramGroup;
    }
    
    // Parameter의 타입 확인 (Category나 Context 정보를 통해 판단)
    private boolean isParameterType(Parameter parameter, String expectedType) {
        // Category나 Context의 name을 통해 파라미터 타입 판단
        if (parameter.getCategory() != null && parameter.getCategory().getName() != null) {
            return parameter.getCategory().getName().contains(expectedType);
        }
        if (parameter.getContext() != null && parameter.getContext().getName() != null) {
            return parameter.getContext().getName().contains(expectedType);
        }
        return false;
    }
    
    // Parameter → ParameterDetail 변환
    private ProjectResponse.ParameterDetail convertToParameterDetail(Parameter parameter) {
        ProjectResponse.ParameterDetail detail = new ProjectResponse.ParameterDetail();
        detail.setKorName(parameter.getNameKo());
        detail.setName(parameter.getName());
        detail.setDataType(parameter.getDataType());
        detail.setLength(parameter.getLength() != null ? parameter.getLength().toString() : null);
        detail.setFormat(parameter.getFormat());
        detail.setDefaultValue(parameter.getDefaultValue());
        detail.setRequired(parameter.getRequired() ? "Y" : "N");
        detail.setDesc(parameter.getDescription());
        return detail;
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

    // 요구사항 분류 정보 처리
    private void processRequirementClassification(Request reqEntity, SaveProjectRequest.RequirementItem reqItem) {
        try {
            // Priority 처리
            if (reqItem.getPriority() != null && !reqItem.getPriority().trim().isEmpty()) {
                Priority priority = findOrCreatePriority(reqItem.getPriority().trim());
                if (priority != null) {
                    reqEntity.setPriorityKey(priority.getPriorityKey());
                    log.debug("Priority 설정: {} -> key: {}", priority.getName(), priority.getPriorityKey());
                }
            }
            
            // RequestMajor 처리
            if (reqItem.getMajor() != null && !reqItem.getMajor().trim().isEmpty()) {
                RequestMajor major = findOrCreateRequestMajor(reqItem.getMajor().trim());
                if (major != null) {
                    reqEntity.setMajorKey(major.getMajorKey());
                    log.debug("Major 설정: {} -> key: {}", major.getName(), major.getMajorKey());
                }
            }
            
            // RequestMiddle 처리
            if (reqItem.getMiddle() != null && !reqItem.getMiddle().trim().isEmpty()) {
                RequestMiddle middle = findOrCreateRequestMiddle(reqItem.getMiddle().trim());
                if (middle != null) {
                    reqEntity.setMiddleKey(middle.getMiddleKey());
                    log.debug("Middle 설정: {} -> key: {}", middle.getName(), middle.getMiddleKey());
                }
            }
            
            // RequestMinor 처리
            if (reqItem.getMinor() != null && !reqItem.getMinor().trim().isEmpty()) {
                RequestMinor minor = findOrCreateRequestMinor(reqItem.getMinor().trim());
                if (minor != null) {
                    reqEntity.setMinorKey(minor.getMinorKey());
                    log.debug("Minor 설정: {} -> key: {}", minor.getName(), minor.getMinorKey());
                }
            }
            
        } catch (Exception e) {
            log.warn("요구사항 분류 정보 처리 중 오류 발생: {}", e.getMessage());
            // 분류 정보 처리 실패해도 요구사항 자체 저장은 계속 진행
        }
    }
    
    // Priority 조회 또는 생성
    private Priority findOrCreatePriority(String priorityName) {
        try {
            return priorityRepository.findByName(priorityName).orElseGet(() -> {
                Priority newPriority = new Priority();
                newPriority.setName(priorityName);
                Priority saved = priorityRepository.save(newPriority);
                log.info("새 Priority 생성: {} -> key: {}", priorityName, saved.getPriorityKey());
                return saved;
            });
        } catch (Exception e) {
            log.error("Priority 처리 실패: {} - {}", priorityName, e.getMessage());
            return null;
        }
    }
    
    // RequestMajor 조회 또는 생성
    private RequestMajor findOrCreateRequestMajor(String majorName) {
        try {
            return requestMajorRepository.findByName(majorName).orElseGet(() -> {
                RequestMajor newMajor = new RequestMajor();
                newMajor.setName(majorName);
                RequestMajor saved = requestMajorRepository.save(newMajor);
                log.info("새 RequestMajor 생성: {} -> key: {}", majorName, saved.getMajorKey());
                return saved;
            });
        } catch (Exception e) {
            log.error("RequestMajor 처리 실패: {} - {}", majorName, e.getMessage());
            return null;
        }
    }
    
    // RequestMiddle 조회 또는 생성
    private RequestMiddle findOrCreateRequestMiddle(String middleName) {
        try {
            return requestMiddleRepository.findByName(middleName).orElseGet(() -> {
                RequestMiddle newMiddle = new RequestMiddle();
                newMiddle.setName(middleName);
                RequestMiddle saved = requestMiddleRepository.save(newMiddle);
                log.info("새 RequestMiddle 생성: {} -> key: {}", middleName, saved.getMiddleKey());
                return saved;
            });
        } catch (Exception e) {
            log.error("RequestMiddle 처리 실패: {} - {}", middleName, e.getMessage());
            return null;
        }
    }
    
    // RequestMinor 조회 또는 생성
    private RequestMinor findOrCreateRequestMinor(String minorName) {
        try {
            return requestMinorRepository.findByName(minorName).orElseGet(() -> {
                RequestMinor newMinor = new RequestMinor();
                newMinor.setName(minorName);
                RequestMinor saved = requestMinorRepository.save(newMinor);
                log.info("새 RequestMinor 생성: {} -> key: {}", minorName, saved.getMinorKey());
                return saved;
            });
        } catch (Exception e) {
            log.error("RequestMinor 처리 실패: {} - {}", minorName, e.getMessage());
            return null;
        }
    }
}