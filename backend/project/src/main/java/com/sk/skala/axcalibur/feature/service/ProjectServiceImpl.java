package com.sk.skala.axcalibur.feature.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sk.skala.axcalibur.feature.dto.ApiInfoDTO;
import com.sk.skala.axcalibur.feature.dto.CreateProjectRequestDTO;
import com.sk.skala.axcalibur.feature.dto.CreateProjectResponseDTO;
import com.sk.skala.axcalibur.feature.dto.DeleteProjectResponseDTO;
import com.sk.skala.axcalibur.feature.dto.ParameterDetailDTO;
import com.sk.skala.axcalibur.feature.dto.ProjectResponseDTO;
import com.sk.skala.axcalibur.feature.dto.RequirementInfoDTO;
import com.sk.skala.axcalibur.feature.dto.SaveProjectRequestDTO;
import com.sk.skala.axcalibur.feature.dto.SaveProjectResponseDTO;
import com.sk.skala.axcalibur.feature.entity.ProjectEntity;
import com.sk.skala.axcalibur.feature.entity.RequestEntity;
import com.sk.skala.axcalibur.feature.entity.ApiListEntity;
import com.sk.skala.axcalibur.feature.entity.ParameterEntity;
import com.sk.skala.axcalibur.feature.entity.CategoryEntity;
import com.sk.skala.axcalibur.feature.entity.ContextEntity;
import com.sk.skala.axcalibur.feature.entity.PriorityEntity;
import com.sk.skala.axcalibur.feature.entity.RequestMajorEntity;
import com.sk.skala.axcalibur.feature.entity.RequestMiddleEntity;
import com.sk.skala.axcalibur.feature.entity.RequestMinorEntity;
import com.sk.skala.axcalibur.feature.entity.redis.AvalonCookieEntity;
import com.sk.skala.axcalibur.feature.repository.ProjectRepository;
import com.sk.skala.axcalibur.feature.repository.RequestRepository;
import com.sk.skala.axcalibur.feature.repository.redis.AvalonCookieRepository;
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
public class ProjectServiceImpl {

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
    private final AvalonCookieRepository avalonCookieRepository;

    // 프로젝트 생성 (IF-PR-0004)
    @Transactional
    public CreateProjectResponseDTO createProject(CreateProjectRequestDTO request) {
        log.debug("프로젝트 생성 시작. projectId: {}", request.getProjectId());
        String projectId = request.getProjectId();
        
        ProjectEntity project = projectRepository.findById(projectId).orElseGet(() -> {
            ProjectEntity newProject = new ProjectEntity();
            newProject.setId(projectId);
            return projectRepository.save(newProject);
        });

        String avalon = generateUUID7Cookie();
        // project.setAvalon(avalon);                  // redis 관련 코드 추가 시 개선 예정
        projectRepository.save(project);
        log.info("Avalon 토큰 생성/업데이트. projectId: {}, newAvalon: {}", projectId, avalon);

        saveAvalonToRedis(avalon, project.getKey()); // Redis 임시 비활성화
        return new CreateProjectResponseDTO(projectId, avalon);
    }

    // IF-PR-0001: 프로젝트 목록 저장
    @Transactional
    public SaveProjectResponseDTO saveProject(String projectId, SaveProjectRequestDTO request) {
        log.info("프로젝트 목록 저장 시작. projectId: {}", projectId);

        ProjectEntity project = projectRepository.findById(projectId).orElseGet(() -> {
            ProjectEntity newProject = new ProjectEntity();
            newProject.setId(projectId);
            log.info("새 프로젝트 생성. projectId: {}", projectId);
            return projectRepository.save(newProject);
        });

        if (request.getRequirement() != null && !request.getRequirement().isEmpty()) {
            for (SaveProjectRequestDTO.RequirementItem reqItem : request.getRequirement()) {
                RequestEntity reqEntity = new RequestEntity();
                reqEntity.setName(reqItem.getName());
                reqEntity.setDescription(reqItem.getDesc());
                reqEntity.setProjectKey(project);

                if (reqItem.getPriority() != null && !reqItem.getPriority().trim().isEmpty()) {
                    PriorityEntity priority = findOrCreatePriority(reqItem.getPriority());
                    reqEntity.setPriorityKey(priority);
                }
                if (reqItem.getMajor() != null && !reqItem.getMajor().trim().isEmpty()) {
                    RequestMajorEntity major = findOrCreateRequestMajor(reqItem.getMajor());
                    reqEntity.setMajorKey(major);
                }
                if (reqItem.getMiddle() != null && !reqItem.getMiddle().trim().isEmpty()) {
                    RequestMiddleEntity middle = findOrCreateRequestMiddle(reqItem.getMiddle());
                    reqEntity.setMiddleKey(middle);
                }
                if (reqItem.getMinor() != null && !reqItem.getMinor().trim().isEmpty()) {
                    RequestMinorEntity minor = findOrCreateRequestMinor(reqItem.getMinor());
                    reqEntity.setMinorKey(minor);
                }
                log.info(" Request 저장 전 확인 - majorKey: {}, middleKey: {}, minorKey: {}, priorityKey: {}", 
                    reqEntity.getMajorKey() != null ? reqEntity.getMajorKey().getKey() : "null",
                    reqEntity.getMiddleKey() != null ? reqEntity.getMiddleKey().getKey() : "null", 
                    reqEntity.getMinorKey() != null ? reqEntity.getMinorKey().getKey() : "null",
                    reqEntity.getPriorityKey() != null ? reqEntity.getPriorityKey().getKey() : "null");
                requestRepository.save(reqEntity);
            }
        }

        if (request.getApiList() != null && !request.getApiList().isEmpty()) {
            for (SaveProjectRequestDTO.ApiItem apiItem : request.getApiList()) {
                ApiListEntity apiEntity = new ApiListEntity();
                apiEntity.setId(apiItem.getId());
                apiEntity.setName(apiItem.getName());
                apiEntity.setDescription(apiItem.getDesc());
                apiEntity.setMethod(apiItem.getMethod());
                apiEntity.setUrl(apiItem.getUrl());
                apiEntity.setPath(apiItem.getPath());
                apiEntity.setProjectKey(project);
                
                ApiListEntity savedApi = apiListRepository.save(apiEntity);
                processApiParameters(savedApi, apiItem);
            }
        }

        log.info("프로젝트 목록 저장 완료. projectId: {}", projectId);
        return new SaveProjectResponseDTO(LocalDateTime.now());
    }

    // 프로젝트 상세 조회 - avalon으로 조회
    public ProjectResponseDTO getProjectDetails(String avalon) {
        log.debug("프로젝트 조회 시작. avalon: {}", avalon);
        
        // 1. Redis에서 avalon 토큰으로 프로젝트 키 찾기
        Optional<AvalonCookieEntity> cookie = avalonCookieRepository.findByToken(avalon);
        if (cookie.isEmpty()) {
            log.warn("유효하지 않은 avalon 토큰: {}", avalon);
            return null;
        }
        
        // 2. 프로젝트 키로 MySQL에서 프로젝트 조회
        Integer projectKey = cookie.get().getProjectKey();
        Optional<ProjectEntity> project = projectRepository.findById(projectKey);
        
        return project
            .map(p -> convertToDetailedResponse(p, avalon))
            .orElse(null);
    }
    

    @Transactional
    public DeleteProjectResponseDTO deleteProject(String projectId) {
        log.info("프로젝트 정보 삭제 시작. projectId: {}", projectId);
        
        Optional<ProjectEntity> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isPresent()) {
            ProjectEntity project = projectOpt.get();
            Integer projectKey = project.getKey(); // 숫자 키 가져오기
            
            // 1. 해당 프로젝트의 API 목록들과 파라미터들 삭제
            List<ApiListEntity> apiLists = apiListRepository.findByProjectKey(project);
            for (ApiListEntity apiList : apiLists) {
                List<ParameterEntity> parameters = parameterRepository.findByApiListKey(apiList);
                parameterRepository.deleteAll(parameters);
            }
            apiListRepository.deleteAll(apiLists);
            
            // 2. 해당 프로젝트의 요구사항들 삭제
            List<RequestEntity> requests = requestRepository.findByProjectKey(project);
            requestRepository.deleteAll(requests);
            
            // 3. Redis에서 해당 프로젝트 쿠키들 삭제
            avalonCookieRepository.deleteByProjectKey(projectKey);
            
            // 4. 프로젝트 삭제
            projectRepository.delete(project);
        }
        
        log.info("프로젝트 정보 삭제 완료. projectId: {}", projectId);
        return new DeleteProjectResponseDTO();
    }

    // IF-PR-0005: 프로젝트 쿠키 삭제
    @Transactional
    public DeleteProjectResponseDTO deleteProjectCookie(String avalon) {
        log.info("프로젝트 쿠키 삭제 요청. avalon: {}", avalon);
        if (avalon != null && !avalon.trim().isEmpty()) {
            avalonCookieRepository.deleteByToken(avalon);
            log.info("Redis 비활성화 상태 - 쿠키 삭제 요청만 처리");
        }
        return new DeleteProjectResponseDTO();
    }

    @Transactional
    private void saveAvalonToRedis(String avalon, Integer projectKey) {
        avalonCookieRepository.save(AvalonCookieEntity.builder()
            .token(avalon)
            .projectKey(projectKey)
            .build());
    }
    

    private String generateUUID7Cookie() {
        return UUID.randomUUID().toString();
    }
    
    private ProjectResponseDTO convertToDetailedResponse(ProjectEntity project, String avalon) {
        String projectName = project.getId() + "_Project";
        
        List<String> specList = List.of("요구사항명세서", "인터페이스정의서", "인터페이스설계서");
    
        // OneToMany 관계 대신 Repository로 직접 조회
        List<RequestEntity> requests = requestRepository.findByProjectKey(project);
        List<RequirementInfoDTO> requirements = requests.stream()
                .map(req -> new RequirementInfoDTO(
                        req.getKey().longValue(),
                        req.getName(),
                        req.getDescription(),
                        req.getPriorityKey() != null ? req.getPriorityKey().getName() : "중요도미정",
                        req.getMajorKey() != null ? req.getMajorKey().getName() : "대분류미정",
                        req.getMiddleKey() != null ? req.getMiddleKey().getName() : "중분류미정",
                        req.getMinorKey() != null ? req.getMinorKey().getName() : "소분류미정"))
                .collect(Collectors.toList());
    
        // OneToMany 관계 대신 Repository로 직접 조회
        List<ApiListEntity> apiLists = apiListRepository.findByProjectKey(project);
        List<ApiInfoDTO> apiInfos = apiLists.stream()
                .map(this::convertApiEntityToDto)
                .collect(Collectors.toList());
    
        return new ProjectResponseDTO(project.getId(), avalon,  // 파라미터로 받은 avalon 사용
                projectName, specList, requirements, apiInfos);
    }

    private ApiInfoDTO convertApiEntityToDto(ApiListEntity api) {
        ApiInfoDTO apiInfo = new ApiInfoDTO();
        apiInfo.setApiPk(api.getKey().longValue());
        apiInfo.setId(api.getId());
        apiInfo.setName(api.getName());
        apiInfo.setDesc(api.getDescription());
        apiInfo.setMethod(api.getMethod());
        apiInfo.setUrl(api.getUrl());
        apiInfo.setPath(api.getPath());

        List<ParameterEntity> parameters = parameterRepository.findByApiListKey(api);
        
        List<ParameterDetailDTO> pathQueryParams = parameters.stream()
            .filter(p -> p.getCategoryKey() != null && (p.getCategoryKey().getName().endsWith("_PQ")))
            .map(this::convertParameterEntityToDto).collect(Collectors.toList());
        apiInfo.setPathQuery(pathQueryParams);

        List<ParameterDetailDTO> requestParams = parameters.stream()
            .filter(p -> p.getCategoryKey() != null && (p.getCategoryKey().getName().endsWith("_REQ")))
            .map(this::convertParameterEntityToDto).collect(Collectors.toList());
        apiInfo.setRequest(requestParams);

        List<ParameterDetailDTO> responseParams = parameters.stream()
            .filter(p -> p.getCategoryKey() != null && (p.getCategoryKey().getName().endsWith("_RES")))
            .map(this::convertParameterEntityToDto).collect(Collectors.toList());
        apiInfo.setResponse(responseParams);

        return apiInfo;
    }

    private ParameterDetailDTO convertParameterEntityToDto(ParameterEntity param) {
        ParameterDetailDTO detail = new ParameterDetailDTO();
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

    private void processApiParameters(ApiListEntity apiList, SaveProjectRequestDTO.ApiItem apiItem) {
        if (apiItem.getRequest() != null) {
            processParameterGroup(apiList, apiItem.getRequest(), "REQUEST");
        }
        if (apiItem.getResponse() != null) {
            processParameterGroup(apiList, apiItem.getResponse(), "RESPONSE");
        }
    }

    private void processParameterGroup(ApiListEntity apiList, SaveProjectRequestDTO.ParameterGroup paramGroup, String groupType) {
        if (paramGroup.getPq() != null) {
            processParameterItems(apiList, paramGroup.getPq(), groupType);
        }
        if (paramGroup.getReq() != null) {
            processParameterItems(apiList, paramGroup.getReq(), groupType);
        }
        if (paramGroup.getRes() != null) {
            processParameterItems(apiList, paramGroup.getRes(), groupType);
        }
    }

    @Transactional
    private void processParameterItems(ApiListEntity apiList, List<SaveProjectRequestDTO.ParameterItem> paramItems, String paramType) {
        for (SaveProjectRequestDTO.ParameterItem paramItem : paramItems) {
            ParameterEntity parameter = new ParameterEntity();
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

    private void setupCategoryAndContext(ParameterEntity parameter, String parameterType) {
        parameter.setCategoryKey(findOrCreateCategory(parameterType));
        parameter.setContextKey(findOrCreateContext(parameterType));
    }

    @Transactional
    private CategoryEntity findOrCreateCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> {
                    CategoryEntity entity = new CategoryEntity();
                    entity.setName(name);
                    return categoryRepository.save(entity);
                });
    }

    @Transactional
    private ContextEntity findOrCreateContext(String name) {
        return contextRepository.findByName(name)
                .orElseGet(() -> {
                    ContextEntity entity = new ContextEntity();
                    entity.setName(name);
                    return contextRepository.save(entity);
                });
    }

    @Transactional
    private PriorityEntity findOrCreatePriority(String name) {
        PriorityEntity entity = priorityRepository.findByName(name).orElse(null);
        if (entity == null) {
            entity = new PriorityEntity();
            entity.setName(name);
            entity.setCreatedAt(LocalDateTime.now());
            entity = priorityRepository.save(entity);
        }
        log.info("Priority 처리 완료 - name: {}, key: {}", entity.getName(), entity.getKey());
        return entity;
    }

    @Transactional
    private RequestMajorEntity findOrCreateRequestMajor(String name) {
        RequestMajorEntity entity = requestMajorRepository.findByName(name).orElse(null);
        if (entity == null) {
            entity = new RequestMajorEntity();
            entity.setName(name);
            entity.setCreatedAt(LocalDateTime.now());
            entity = requestMajorRepository.save(entity);
        }
        log.info("RequestMajor 처리 완료 - name: {}, key: {}", entity.getName(), entity.getKey());
        return entity;
    }

    @Transactional
    private RequestMiddleEntity findOrCreateRequestMiddle(String name) {
        RequestMiddleEntity entity = requestMiddleRepository.findByName(name).orElse(null);
        if (entity == null) {
            entity = new RequestMiddleEntity();
            entity.setName(name);
            entity.setCreatedAt(LocalDateTime.now());
            entity = requestMiddleRepository.save(entity);
        }
        log.info("RequestMiddle 처리 완료 - name: {}, key: {}", entity.getName(), entity.getKey());
        return entity;
    }

    @Transactional
    private RequestMinorEntity findOrCreateRequestMinor(String name) {
        RequestMinorEntity entity = requestMinorRepository.findByName(name).orElse(null);
        if (entity == null) {
            entity = new RequestMinorEntity();
            entity.setName(name);
            entity.setCreatedAt(LocalDateTime.now());
            entity = requestMinorRepository.save(entity);
        }
        log.info("RequestMinor 처리 완료 - name: {}, key: {}", entity.getName(), entity.getKey());
        return entity;
    }
}