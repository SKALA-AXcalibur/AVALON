package com.sk.skala.axcalibur.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

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
    private final RedisTemplate<String, String> redisTemplate;

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
            
            // 매번 새로운 avalon 생성 (사용자별 고유 세션)
            String avalon = generateUUID7Cookie();
            
            // DB의 avalon 값도 업데이트
            existingProject.setAvalon(avalon);
            projectRepository.save(existingProject);
            log.info("기존 프로젝트 avalon 업데이트. projectId: {}, newAvalon: {}", projectId, avalon);
            
            // Redis에 인증정보 저장 (30분 TTL)
            try {
                String redisKey = "auth:avalon:" + avalon;
                redisTemplate.opsForValue().set(redisKey, projectId, Duration.ofMinutes(30));
                log.info("Redis에 인증정보 저장 완료. key: {}, projectId: {}", redisKey, projectId);
            } catch (Exception e) {
                log.warn("Redis 인증정보 저장 실패. avalon: {}, 오류: {}", avalon, e.getMessage());
            }
            
            return new ProjectResponse(requestTime, projectId, avalon);
        }
        
        // 3. 새 Project 생성
        Project project = new Project();
        project.setProjectId(projectId);
        
        // 4. avalon 값 생성 및 저장
        String avalon = generateUUID7Cookie();
        project.setAvalon(avalon);
        
        Project savedProject = projectRepository.save(project);
        
        // 5. Redis에 인증정보 저장 (30분 TTL)
        try {
            String redisKey = "auth:avalon:" + avalon;
            redisTemplate.opsForValue().set(redisKey, projectId, Duration.ofMinutes(30));
            log.info("Redis에 인증정보 저장 완료. key: {}, projectId: {}", redisKey, projectId);
        } catch (Exception e) {
            log.warn("Redis 인증정보 저장 실패. avalon: {}, 오류: {}", avalon, e.getMessage());
        }
        
        // 6. CreateProjectRequest는 단순 생성용이므로 추가 데이터 처리 없음
        log.debug("간단 프로젝트 생성 - 추가 데이터 처리 생략");
        
        // 7. 응답 생성 (설계서 기준)
        String requestTime = LocalDateTime.now().toString();
        
        log.info("새 프로젝트 생성 완료. projectId: {}, avalon: {}", savedProject.getProjectId(), avalon);
        return new ProjectResponse(requestTime, savedProject.getProjectId(), avalon);
    }

    // ==================== 설계서 기준 새로운 API 메서드들 ====================
    
    // IF-PR-0001: 프로젝트 목록 저장
    @Transactional
    public SaveProjectResponse saveProject(String projectId, SaveProjectRequest request) {
        log.info("프로젝트 목록 저장 시작. projectId: {}", projectId);
        
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
                
                // 분류 정보를 실제 키 값으로 설정
                if (reqItem.getPriority() != null && !reqItem.getPriority().trim().isEmpty()) {
                    Priority priority = findOrCreatePriority(reqItem.getPriority());
                    reqEntity.setPriorityKey(priority.getPriorityKey());
                    log.debug("우선순위 설정: {} -> key: {}", reqItem.getPriority(), priority.getPriorityKey());
                }
                
                if (reqItem.getMajor() != null && !reqItem.getMajor().trim().isEmpty()) {
                    RequestMajor major = findOrCreateRequestMajor(reqItem.getMajor());
                    reqEntity.setMajorKey(major.getMajorKey());
                    log.debug("대분류 설정: {} -> key: {}", reqItem.getMajor(), major.getMajorKey());
                }
                
                if (reqItem.getMiddle() != null && !reqItem.getMiddle().trim().isEmpty()) {
                    RequestMiddle middle = findOrCreateRequestMiddle(reqItem.getMiddle());
                    reqEntity.setMiddleKey(middle.getMiddleKey());
                    log.debug("중분류 설정: {} -> key: {}", reqItem.getMiddle(), middle.getMiddleKey());
                }
                
                if (reqItem.getMinor() != null && !reqItem.getMinor().trim().isEmpty()) {
                    RequestMinor minor = findOrCreateRequestMinor(reqItem.getMinor());
                    reqEntity.setMinorKey(minor.getMinorKey());
                    log.debug("소분류 설정: {} -> key: {}", reqItem.getMinor(), minor.getMinorKey());
                }
                
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
    }

    // IF-PR-0002: 프로젝트 목록 조회
    public Object getProjectList(String avalon) {
        log.info("프로젝트 목록 조회 API 호출. avalon: '{}', null 여부: {}, 빈 문자열 여부: {}", 
                avalon, avalon == null, avalon != null && avalon.trim().isEmpty());
        
        try {
            // avalon 값이 있으면 해당 avalon과 일치하는 프로젝트만 조회
            if (avalon != null && !avalon.trim().isEmpty()) {
                log.info("avalon 값이 유효함. 특정 프로젝트 조회 시작. avalon: '{}'", avalon);
                Project project = projectRepository.findByAvalon(avalon).orElse(null);
                
                if (project != null) {
                    ProjectResponse detailedProject = convertToDetailedResponse(project);
                    log.info("avalon 기반 프로젝트 조회 완료. projectId: {}", project.getProjectId());
                    return List.of(detailedProject);
                } else {
                    log.warn("avalon 값과 일치하는 프로젝트를 찾을 수 없습니다. avalon: '{}'", avalon);
                    return new ArrayList<>();
                }
            }
            
            // avalon 값이 없으면 빈 리스트 반환 (전체 조회 안함)
            log.info("avalon 값이 null이거나 빈 문자열임. 빈 리스트 반환");
            return new ArrayList<>();
            
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
            // Redis에서 인증정보 삭제
            if (avalon != null && !avalon.trim().isEmpty()) {
                String redisKey = "auth:avalon:" + avalon;
                Boolean deleted = redisTemplate.delete(redisKey);
                log.info("Redis 인증정보 삭제 {}. key: {}", deleted ? "성공" : "실패 또는 키 없음", redisKey);
            }
            
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
        String avalon = project.getAvalon();
        if (avalon == null) {
            avalon = generateUUID7Cookie();
            project.setAvalon(avalon);
            projectRepository.save(project);
        }
        return new ProjectResponse(requestTime, project.getProjectId(), avalon);
    }

    // Project → 상세 ProjectResponse 변환 (명세서 분석 정보 포함)
    private ProjectResponse convertToDetailedResponse(Project project) {
        String requestTime = LocalDateTime.now().toString();
        String avalon = project.getAvalon();
        if (avalon == null) {
            avalon = generateUUID7Cookie();
            project.setAvalon(avalon);
            projectRepository.save(project);
        }
        
        // 명세서 분석 정보 구성
        String projectName = project.getProjectId() + "_Project";
        List<String> specList = List.of("요구사항명세서", "인터페이스정의서", "인터페이스설계서");
        
        // 요구사항 정보 조회 - 실제 저장된 분류 정보 사용
        List<ProjectResponse.RequirementInfo> requirements = project.getRequirements().stream()
            .map(req -> {
                String priorityName = req.getPriority() != null ? req.getPriority().getName() : "중요도미정";
                String majorName = req.getRequestMajor() != null ? req.getRequestMajor().getName() : "대분류미정";
                String middleName = req.getRequestMiddle() != null ? req.getRequestMiddle().getName() : "중분류미정";
                String minorName = req.getRequestMinor() != null ? req.getRequestMinor().getName() : "소분류미정";
                
                return new ProjectResponse.RequirementInfo(
                    req.getName(), 
                    req.getDescription(), 
                    priorityName, majorName, middleName, minorName);
            })
            .collect(Collectors.toList());
        
        // API 정보 조회 - 실제 저장된 파라미터 정보 사용
        List<ProjectResponse.ApiInfo> apiInfos = project.getApiLists().stream()
            .map(api -> {
                ProjectResponse.ApiInfo apiInfo = new ProjectResponse.ApiInfo();
                apiInfo.setId(api.getApiListId());
                apiInfo.setName(api.getName());
                apiInfo.setDesc(api.getDescription());
                apiInfo.setMethod(api.getMethod());
                apiInfo.setUrl(api.getUrl());
                apiInfo.setPath(api.getPath());
                
                // PathQuery 정보 구성 (실제 파라미터 기반)
                String pathQuery = buildPathQueryFromParameters(api);
                apiInfo.setPathQuery(pathQuery);
                
                // 파라미터 정보 구성 (실제 저장된 파라미터 기반)
                ProjectResponse.ParameterGroup request = buildParameterGroupFromApi(api, "REQUEST");
                ProjectResponse.ParameterGroup response = buildParameterGroupFromApi(api, "RESPONSE");
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

    // 우선순위 조회 또는 생성
    private Priority findOrCreatePriority(String name) {
        return priorityRepository.findByName(name)
            .orElseGet(() -> {
                Priority priority = new Priority();
                priority.setName(name);
                return priorityRepository.save(priority);
            });
    }
    
    // 대분류 조회 또는 생성
    private RequestMajor findOrCreateRequestMajor(String name) {
        return requestMajorRepository.findByName(name)
            .orElseGet(() -> {
                RequestMajor major = new RequestMajor();
                major.setName(name);
                return requestMajorRepository.save(major);
            });
    }
    
    // 중분류 조회 또는 생성
    private RequestMiddle findOrCreateRequestMiddle(String name) {
        return requestMiddleRepository.findByName(name)
            .orElseGet(() -> {
                RequestMiddle middle = new RequestMiddle();
                middle.setName(name);
                return requestMiddleRepository.save(middle);
            });
    }
    
    // 소분류 조회 또는 생성
    private RequestMinor findOrCreateRequestMinor(String name) {
        return requestMinorRepository.findByName(name)
            .orElseGet(() -> {
                RequestMinor minor = new RequestMinor();
                minor.setName(name);
                return requestMinorRepository.save(minor);
            });
    }
    
    // API의 파라미터 정보를 기반으로 PathQuery 문자열 구성
    private String buildPathQueryFromParameters(ApiList api) {
        List<Parameter> pathParams = api.getParameters().stream()
            .filter(param -> {
                String categoryName = param.getCategory() != null ? param.getCategory().getName() : "";
                String contextName = param.getContext() != null ? param.getContext().getName() : "";
                return "PATH".equals(categoryName) || "QUERY".equals(categoryName) || 
                       "PATH".equals(contextName) || "QUERY".equals(contextName);
            })
            .collect(Collectors.toList());
            
        if (pathParams.isEmpty()) {
            return "PathQuery미정";
        }
        
        return pathParams.stream()
            .map(param -> param.getName() + "=" + param.getDataType())
            .collect(Collectors.joining("&"));
    }
    
    // API의 파라미터 정보를 기반으로 ParameterGroup 구성
    private ProjectResponse.ParameterGroup buildParameterGroupFromApi(ApiList api, String groupType) {
        ProjectResponse.ParameterGroup paramGroup = new ProjectResponse.ParameterGroup();
        
        List<Parameter> filteredParams = api.getParameters().stream()
            .filter(param -> {
                String categoryName = param.getCategory() != null ? param.getCategory().getName() : "";
                String contextName = param.getContext() != null ? param.getContext().getName() : "";
                
                if (groupType.equals("REQUEST")) {
                    return "BODY".equals(categoryName) || "HEADER".equals(categoryName) || "PATH".equals(categoryName) || "QUERY".equals(categoryName) ||
                           "BODY".equals(contextName) || "HEADER".equals(contextName) || "PATH".equals(contextName) || "QUERY".equals(contextName);
                } else if (groupType.equals("RESPONSE")) {
                    return "RESPONSE".equals(categoryName) || "RESPONSE".equals(contextName);
                }
                return false;
            })
            .collect(Collectors.toList());
        
        if (filteredParams.isEmpty()) {
            // 기본 빈 구조 반환
            paramGroup.setPq(new ArrayList<>());
            paramGroup.setReq(new ArrayList<>());
            paramGroup.setRes(new ArrayList<>());
            return paramGroup;
        }
        
        // 파라미터 타입별로 분류하여 설정
        List<ProjectResponse.ParameterDetail> pqItems = new ArrayList<>();
        List<ProjectResponse.ParameterDetail> reqItems = new ArrayList<>();
        List<ProjectResponse.ParameterDetail> resItems = new ArrayList<>();
        
        for (Parameter param : filteredParams) {
            ProjectResponse.ParameterDetail item = new ProjectResponse.ParameterDetail();
            item.setKorName(param.getNameKo());
            item.setName(param.getName());
            item.setDataType(param.getDataType());
            item.setRequired(param.getRequired() != null && param.getRequired() ? "Y" : "N");
            item.setDesc(param.getDescription());
            item.setLength(param.getLength() != null ? param.getLength().toString() : "");
            item.setFormat(param.getFormat());
            item.setDefaultValue(param.getDefaultValue());
            
            String categoryName = param.getCategory() != null ? param.getCategory().getName() : "";
            String contextName = param.getContext() != null ? param.getContext().getName() : "";
            
            if ("PATH".equals(categoryName) || "QUERY".equals(categoryName) || 
                "PATH".equals(contextName) || "QUERY".equals(contextName)) {
                pqItems.add(item);
            } else if (groupType.equals("REQUEST") && 
                      ("BODY".equals(categoryName) || "HEADER".equals(categoryName) || 
                       "BODY".equals(contextName) || "HEADER".equals(contextName))) {
                reqItems.add(item);
            } else if (groupType.equals("RESPONSE") && 
                      ("RESPONSE".equals(categoryName) || "RESPONSE".equals(contextName))) {
                resItems.add(item);
            }
        }
        
        paramGroup.setPq(pqItems);
        paramGroup.setReq(reqItems);
        paramGroup.setRes(resItems);
        
        return paramGroup;
    }
}