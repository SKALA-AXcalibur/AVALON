package com.sk.skala.axcalibur.feature.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sk.skala.axcalibur.feature.dto.ApiInfo;
import com.sk.skala.axcalibur.feature.dto.CreateProjectRequest;
import com.sk.skala.axcalibur.feature.dto.CreateProjectResponse;
import com.sk.skala.axcalibur.feature.dto.DeleteProjectResponse;
import com.sk.skala.axcalibur.feature.dto.ParameterDetail;
import com.sk.skala.axcalibur.feature.dto.ProjectResponse;
import com.sk.skala.axcalibur.feature.dto.RequirementInfo;
import com.sk.skala.axcalibur.feature.dto.SaveProjectRequest;
import com.sk.skala.axcalibur.feature.dto.SaveProjectResponse;
import com.sk.skala.axcalibur.feature.entity.Project;
import com.sk.skala.axcalibur.feature.entity.Request;
import com.sk.skala.axcalibur.feature.entity.ApiList;
import com.sk.skala.axcalibur.feature.entity.Parameter;
import com.sk.skala.axcalibur.feature.entity.Category;
import com.sk.skala.axcalibur.feature.entity.Context;
import com.sk.skala.axcalibur.feature.entity.Priority;
import com.sk.skala.axcalibur.feature.entity.RequestMajor;
import com.sk.skala.axcalibur.feature.entity.RequestMiddle;
import com.sk.skala.axcalibur.feature.entity.RequestMinor;
import com.sk.skala.axcalibur.feature.repository.ProjectRepository;
import com.sk.skala.axcalibur.feature.repository.RequestRepository;
import com.sk.skala.axcalibur.feature.repository.ApiListRepository;
import com.sk.skala.axcalibur.feature.repository.ParameterRepository;
import com.sk.skala.axcalibur.feature.repository.CategoryRepository;
import com.sk.skala.axcalibur.feature.repository.ContextRepository;
import com.sk.skala.axcalibur.feature.repository.PriorityRepository;
import com.sk.skala.axcalibur.feature.repository.RequestMajorRepository;
import com.sk.skala.axcalibur.feature.repository.RequestMiddleRepository;
import com.sk.skala.axcalibur.feature.repository.RequestMinorRepository;
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
    // private final RedisTemplate<String, Object> redisTemplate; // Redis 임시 비활성화

    // 프로젝트 생성 (IF-PR-0004)
    @Transactional
    public CreateProjectResponse createProject(CreateProjectRequest request) {
        log.debug("프로젝트 생성 시작. projectId: {}", request.getProjectId());
        String projectId = request.getProjectId();
        
        Project project = projectRepository.findById(projectId).orElseGet(() -> {
            Project newProject = new Project();
            newProject.setId(projectId);
            return projectRepository.save(newProject);
        });

        String avalon = generateUUID7Cookie();
        // project.setAvalon(avalon);                  // redis 관련 코드 추가 시 개선 예정
        projectRepository.save(project);
        log.info("Avalon 토큰 생성/업데이트. projectId: {}, newAvalon: {}", projectId, avalon);

        // saveAvalonToRedis(avalon, projectId); // Redis 임시 비활성화
        return new CreateProjectResponse(projectId, avalon);
    }

    // IF-PR-0001: 프로젝트 목록 저장
    @Transactional
    public SaveProjectResponse saveProject(String projectId, SaveProjectRequest request) {
        log.info("프로젝트 목록 저장 시작. projectId: {}", projectId);

        Project project = projectRepository.findById(projectId).orElseGet(() -> {
            Project newProject = new Project();
            newProject.setId(projectId);
            log.info("새 프로젝트 생성. projectId: {}", projectId);
            return projectRepository.save(newProject);
        });

        if (request.getRequirement() != null && !request.getRequirement().isEmpty()) {
            for (SaveProjectRequest.RequirementItem reqItem : request.getRequirement()) {
                Request reqEntity = new Request();
                reqEntity.setName(reqItem.getName());
                reqEntity.setDescription(reqItem.getDesc());
                reqEntity.setProjectKey(project);

                if (reqItem.getPriority() != null && !reqItem.getPriority().trim().isEmpty()) {
                    reqEntity.setPriorityKey(findOrCreatePriority(reqItem.getPriority()));
                }
                if (reqItem.getMajor() != null && !reqItem.getMajor().trim().isEmpty()) {
                    reqEntity.setMajorKey(findOrCreateRequestMajor(reqItem.getMajor()));
                }
                if (reqItem.getMiddle() != null && !reqItem.getMiddle().trim().isEmpty()) {
                    reqEntity.setMiddleKey(findOrCreateRequestMiddle(reqItem.getMiddle()));
                }
                if (reqItem.getMinor() != null && !reqItem.getMinor().trim().isEmpty()) {
                    reqEntity.setMinorKey(findOrCreateRequestMinor(reqItem.getMinor()));
                }
                requestRepository.save(reqEntity);
            }
        }

        if (request.getApiList() != null && !request.getApiList().isEmpty()) {
            for (SaveProjectRequest.ApiItem apiItem : request.getApiList()) {
                ApiList apiEntity = new ApiList();
                apiEntity.setId(apiItem.getId());
                apiEntity.setName(apiItem.getName());
                apiEntity.setDescription(apiItem.getDesc());
                apiEntity.setMethod(apiItem.getMethod());
                apiEntity.setUrl(apiItem.getUrl());
                apiEntity.setPath(apiItem.getPath());
                apiEntity.setProjectKey(project);
                
                ApiList savedApi = apiListRepository.save(apiEntity);
                processApiParameters(savedApi, apiItem);
            }
        }

        log.info("프로젝트 목록 저장 완료. projectId: {}", projectId);
        return new SaveProjectResponse(LocalDateTime.now());
    }

    // 프로젝트 상세 조회 - avalon으로 조회
    public ProjectResponse getProjectDetails(String avalon) {
        log.debug("프로젝트 조회 시작. avalon: {}", avalon);
        return projectRepository.findByAvalon(avalon)
                .map(this::convertToDetailedResponse)
                .orElse(null);
    }

    // IF-PR-0003: 프로젝트 정보 삭제
    @Transactional
    public DeleteProjectResponse deleteProject(String projectId) {
        log.info("프로젝트 정보 삭제 시작. projectId: {}", projectId);
        projectRepository.findById(projectId).ifPresent(projectRepository::delete);
        log.info("프로젝트 정보 삭제 완료. projectId: {}", projectId);
        return new DeleteProjectResponse();
    }

    // IF-PR-0005: 프로젝트 쿠키 삭제
    public DeleteProjectResponse deleteProjectCookie(String avalon) {
        log.info("프로젝트 쿠키 삭제 요청. avalon: {}", avalon);
        if (avalon != null && !avalon.trim().isEmpty()) {
            // String redisKey = "auth:avalon:" + avalon;
            // redisTemplate.delete(redisKey);
            // log.info("Redis 인증정보 삭제 성공. key: {}", redisKey);
            log.info("Redis 비활성화 상태 - 쿠키 삭제 요청만 처리");
        }
        return new DeleteProjectResponse();
    }

    /*
    private void saveAvalonToRedis(String avalon, String projectId) {
        String redisKey = "auth:avalon:" + avalon;
        redisTemplate.opsForValue().set(redisKey, projectId, Duration.ofMinutes(30));
    }
    */

    private String generateUUID7Cookie() {
        return UUID.randomUUID().toString();
    }
    
    private ProjectResponse convertToDetailedResponse(Project project) {
        // String avalon = project.getAvalon();         // redis 관련 코드 추가 시 개선 예정
        // if (avalon == null) {
        //     avalon = generateUUID7Cookie();
        //     project.setAvalon(avalon);
        //     projectRepository.save(project);
        // }

        String projectName = project.getId() + "_Project";
        List<String> specList = List.of("요구사항명세서", "인터페이스정의서", "인터페이스설계서");

        // OneToMany 관계 대신 Repository로 직접 조회
        List<Request> requests = requestRepository.findByProjectKey(project);
        List<RequirementInfo> requirements = requests.stream()
                .map(req -> new RequirementInfo(
                        req.getKey().longValue(),
                        req.getName(),
                        req.getDescription(),
                        req.getPriorityKey() != null ? req.getPriorityKey().getName() : "중요도미정",
                        req.getMajorKey() != null ? req.getMajorKey().getName() : "대분류미정",
                        req.getMiddleKey() != null ? req.getMiddleKey().getName() : "중분류미정",
                        req.getMinorKey() != null ? req.getMinorKey().getName() : "소분류미정"))
                .collect(Collectors.toList());

        // OneToMany 관계 대신 Repository로 직접 조회
        List<ApiList> apiLists = apiListRepository.findByProjectKey(project);
        List<ApiInfo> apiInfos = apiLists.stream()
                .map(this::convertApiEntityToDto)
                .collect(Collectors.toList());

        return new ProjectResponse(project.getId(), avalon,         // redis 관련 코드 추가 시 개선 예정
                projectName, specList, requirements, apiInfos);
    }

    private ApiInfo convertApiEntityToDto(ApiList api) {
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setApiPk(api.getKey().longValue());
        apiInfo.setId(api.getId());
        apiInfo.setName(api.getName());
        apiInfo.setDesc(api.getDescription());
        apiInfo.setMethod(api.getMethod());
        apiInfo.setUrl(api.getUrl());
        apiInfo.setPath(api.getPath());

        List<Parameter> parameters = parameterRepository.findByApiListKey(api);
        
        List<ParameterDetail> pathQueryParams = parameters.stream()
            .filter(p -> p.getCategoryKey() != null && (p.getCategoryKey().getName().endsWith("_PQ")))
            .map(this::convertParameterEntityToDto).collect(Collectors.toList());
        apiInfo.setPathQuery(pathQueryParams);

        List<ParameterDetail> requestParams = parameters.stream()
            .filter(p -> p.getCategoryKey() != null && (p.getCategoryKey().getName().endsWith("_REQ")))
            .map(this::convertParameterEntityToDto).collect(Collectors.toList());
        apiInfo.setRequest(requestParams);

        List<ParameterDetail> responseParams = parameters.stream()
            .filter(p -> p.getCategoryKey() != null && (p.getCategoryKey().getName().endsWith("_RES")))
            .map(this::convertParameterEntityToDto).collect(Collectors.toList());
        apiInfo.setResponse(responseParams);

        return apiInfo;
    }

    private ParameterDetail convertParameterEntityToDto(Parameter param) {
        ParameterDetail detail = new ParameterDetail();
        detail.setParameterId(param.getKey().longValue());
        detail.setKorName(param.getNameKo());
        detail.setName(param.getName());
        detail.setItemType(param.getCategoryKey() != null ? param.getCategoryKey().getName() : null);
        detail.setDataType(param.getDataType());
        detail.setLength(param.getLength());
        detail.setFormat(param.getFormat());
        detail.setDefaultValue(param.getDefaultValue());
        detail.setRequired(param.getRequired());
        detail.setUpper(param.getParentKey() != null ? param.getParentKey().getName() : null);
        detail.setDesc(param.getDescription());
        if (param.getApiListKey() != null) {
            detail.setApiId(param.getApiListKey().getId());
            detail.setApiName(param.getApiListKey().getName());
        }
        return detail;
    }

    private void processApiParameters(ApiList apiList, SaveProjectRequest.ApiItem apiItem) {
        if (apiItem.getRequest() != null) {
            processParameterGroup(apiList, apiItem.getRequest(), "REQUEST");
        }
        if (apiItem.getResponse() != null) {
            processParameterGroup(apiList, apiItem.getResponse(), "RESPONSE");
        }
    }

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

    private void processParameterItems(ApiList apiList, List<SaveProjectRequest.ParameterItem> paramItems, String paramType) {
        for (SaveProjectRequest.ParameterItem paramItem : paramItems) {
            Parameter parameter = new Parameter();
            parameter.setId(paramItem.getName() + "_" + System.currentTimeMillis());
            parameter.setNameKo(paramItem.getKorName());
            parameter.setName(paramItem.getName());
            parameter.setDataType(paramItem.getDataType());
            if (paramItem.getLength() != null && !paramItem.getLength().isBlank()) {
                parameter.setLength(Integer.valueOf(paramItem.getLength()));
            }
            parameter.setFormat(paramItem.getFormat());
            parameter.setDefaultValue(paramItem.getDefaultValue());
            parameter.setRequired("Y".equalsIgnoreCase(paramItem.getRequired()));
            parameter.setDescription(paramItem.getDesc());
            parameter.setApiListKey(apiList);
            setupCategoryAndContext(parameter, paramType);
            parameterRepository.save(parameter);
        }
    }

    private void setupCategoryAndContext(Parameter parameter, String parameterType) {
        parameter.setCategoryKey(findOrCreateCategory(parameterType));
        parameter.setContextKey(findOrCreateContext(parameterType));
    }

    private Category findOrCreateCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> {
                    Category entity = new Category();
                    entity.setName(name);
                    return categoryRepository.save(entity);
                });
    }

    private Context findOrCreateContext(String name) {
        return contextRepository.findByName(name)
                .orElseGet(() -> {
                    Context entity = new Context();
                    entity.setName(name);
                    return contextRepository.save(entity);
                });
    }

    private Priority findOrCreatePriority(String name) {
        return priorityRepository.findByName(name)
                .orElseGet(() -> {
                    Priority entity = new Priority();
                    entity.setName(name);
                    return priorityRepository.save(entity);
                });
    }

    private RequestMajor findOrCreateRequestMajor(String name) {
        return requestMajorRepository.findByName(name)
                .orElseGet(() -> {
                    RequestMajor entity = new RequestMajor();
                    entity.setName(name);
                    return requestMajorRepository.save(entity);
                });
    }

    private RequestMiddle findOrCreateRequestMiddle(String name) {
        return requestMiddleRepository.findByName(name)
                .orElseGet(() -> {
                    RequestMiddle entity = new RequestMiddle();
                    entity.setName(name);
                    return requestMiddleRepository.save(entity);
                });
    }

    private RequestMinor findOrCreateRequestMinor(String name) {
        return requestMinorRepository.findByName(name)
                .orElseGet(() -> {
                    RequestMinor entity = new RequestMinor();
                    entity.setName(name);
                    return requestMinorRepository.save(entity);
                });
    }
}