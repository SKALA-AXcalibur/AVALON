package com.sk.skala.axcalibur.feature.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.uuid.Generators;
import com.sk.skala.axcalibur.feature.dto.ApiInfoDto;
import com.sk.skala.axcalibur.feature.dto.CreateProjectRequestDto;
import com.sk.skala.axcalibur.feature.dto.CreateProjectResponseDto;
import com.sk.skala.axcalibur.feature.dto.DeleteProjectCookieDto;
import com.sk.skala.axcalibur.feature.dto.DeleteProjectResponseDto;
import com.sk.skala.axcalibur.feature.dto.ParameterDetailDto;
import com.sk.skala.axcalibur.feature.dto.ProjectResponseDto;
import com.sk.skala.axcalibur.feature.dto.RequirementInfoDto;
import com.sk.skala.axcalibur.feature.dto.SaveProjectRequestDto;
import com.sk.skala.axcalibur.feature.dto.SaveProjectResponseDto;
import com.sk.skala.axcalibur.feature.dto.item.ApiItem;
import com.sk.skala.axcalibur.feature.dto.item.ParameterGroup;
import com.sk.skala.axcalibur.feature.dto.item.ParameterItem;
import com.sk.skala.axcalibur.feature.dto.item.ReqItem;
import com.sk.skala.axcalibur.feature.entity.ProjectEntity;
import com.sk.skala.axcalibur.feature.entity.RequestEntity;
import com.sk.skala.axcalibur.feature.entity.ApiListEntity;
import com.sk.skala.axcalibur.feature.entity.AvalonCookieEntity;
import com.sk.skala.axcalibur.feature.entity.ParameterEntity;
import com.sk.skala.axcalibur.feature.entity.CategoryEntity;
import com.sk.skala.axcalibur.feature.entity.ContextEntity;
import com.sk.skala.axcalibur.feature.entity.PriorityEntity;
import com.sk.skala.axcalibur.feature.entity.RequestMajorEntity;
import com.sk.skala.axcalibur.feature.entity.RequestMiddleEntity;
import com.sk.skala.axcalibur.feature.entity.RequestMinorEntity;
import com.sk.skala.axcalibur.feature.repository.ProjectRepository;
import com.sk.skala.axcalibur.feature.repository.RequestRepository;
import com.sk.skala.axcalibur.feature.repository.ApiListRepository;
import com.sk.skala.axcalibur.feature.repository.AvalonCookieRepository;
import com.sk.skala.axcalibur.feature.repository.ParameterRepository;
import com.sk.skala.axcalibur.feature.repository.CategoryRepository;
import com.sk.skala.axcalibur.feature.repository.ContextRepository;
import com.sk.skala.axcalibur.feature.repository.PriorityRepository;
import com.sk.skala.axcalibur.feature.repository.RequestMajorRepository;
import com.sk.skala.axcalibur.feature.repository.RequestMiddleRepository;
import com.sk.skala.axcalibur.feature.repository.RequestMinorRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 프로젝트 관리 서비스
// 명세서 기준: 저장, 조회, 삭제, 생성 기능만 제공
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

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
    public CreateProjectResponseDto createProject(CreateProjectRequestDto request) {
        log.debug("프로젝트 생성 시작. projectId: {}", request.getProjectId());
        String projectId = request.getProjectId();
        
        ProjectEntity project = projectRepository.findById(projectId).orElseGet(() -> {
            ProjectEntity newProject = ProjectEntity.builder()
                .id(projectId)
                .build();
            return projectRepository.save(newProject);
        });

        String avalon = generateUUID7Cookie();
        // project.setAvalon(avalon);                  // redis 관련 코드 추가 시 개선 예정
        projectRepository.save(project);
        log.info("Avalon 토큰 생성/업데이트. projectId: {}, newAvalon: {}", projectId, avalon);

        saveAvalonToRedis(avalon, project.getKey()); // Redis 임시 비활성화
        return new CreateProjectResponseDto(projectId, avalon);
    }

@Transactional
public SaveProjectResponseDto saveProject(String projectId, SaveProjectRequestDto request) {
    log.info("프로젝트 목록 저장 시작. projectId: {}", projectId);

    ProjectEntity project = projectRepository.findById(projectId).orElseGet(() -> { 
        ProjectEntity newProject = ProjectEntity.builder()
            .id(projectId)
            .build();
        log.info("새 프로젝트 생성. projectId: {}", projectId);
        return projectRepository.save(newProject);
    });

    if (request.getRequirement() != null && !request.getRequirement().isEmpty()) {
        for (ReqItem reqItem : request.getRequirement()) {

            // id가 없는 경우 UUID 생성
            if (reqItem.getId() == null || reqItem.getId().trim().isEmpty()) {
                throw new BusinessExceptionHandler(ErrorCode.NOT_VALID_ERROR);
            }

            RequestEntity.RequestEntityBuilder reqBuilder = RequestEntity.builder()
                .id(reqItem.getId())
                .name(reqItem.getName())
                .description(reqItem.getDesc())
                .projectKey(project);

            if (reqItem.getPriority() != null && !reqItem.getPriority().trim().isEmpty()) {
                PriorityEntity priority = findOrCreatePriority(reqItem.getPriority());
                reqBuilder.priorityKey(priority);
            }
            if (reqItem.getMajor() != null && !reqItem.getMajor().trim().isEmpty()) {
                RequestMajorEntity major = findOrCreateRequestMajor(reqItem.getMajor());
                reqBuilder.majorKey(major);
            }
            if (reqItem.getMiddle() != null && !reqItem.getMiddle().trim().isEmpty()) {
                RequestMiddleEntity middle = findOrCreateRequestMiddle(reqItem.getMiddle());
                reqBuilder.middleKey(middle);
            }
            if (reqItem.getMinor() != null && !reqItem.getMinor().trim().isEmpty()) {
                RequestMinorEntity minor = findOrCreateRequestMinor(reqItem.getMinor());
                reqBuilder.minorKey(minor);
            }
            
            RequestEntity reqEntity = reqBuilder.build();
            
            log.info(" Request 저장 전 확인 - majorKey: {}, middleKey: {}, minorKey: {}, priorityKey: {}", 
                reqEntity.getMajorKey() != null ? reqEntity.getMajorKey().getKey() : "null",
                reqEntity.getMiddleKey() != null ? reqEntity.getMiddleKey().getKey() : "null", 
                reqEntity.getMinorKey() != null ? reqEntity.getMinorKey().getKey() : "null",
                reqEntity.getPriorityKey() != null ? reqEntity.getPriorityKey().getKey() : "null");
            requestRepository.save(reqEntity);
        }
    }

    if (request.getApiList() != null && !request.getApiList().isEmpty()) {
        for (ApiItem apiItem : request.getApiList()) {
            ApiListEntity apiEntity = ApiListEntity.builder()
                .id(apiItem.getId())
                .name(apiItem.getName())
                .description(apiItem.getDesc())
                .method(apiItem.getMethod())
                .url(apiItem.getUrl())
                .path(apiItem.getPath())
                .projectKey(project)
                .build();
            
            ApiListEntity savedApi = apiListRepository.save(apiEntity);
            processApiParameters(savedApi, apiItem);
        }
    }

    log.info("프로젝트 목록 저장 완료. projectId: {}", projectId);
    return new SaveProjectResponseDto(LocalDateTime.now());
}

    // 프로젝트 상세 조회 - avalon으로 조회
    public ProjectResponseDto getProjectDetails(String avalon) {
        log.debug("프로젝트 조회 시작. avalon: {}", avalon);
        
        // 1. Redis에서 avalon 토큰으로 프로젝트 키 찾기
        Optional<AvalonCookieEntity> cookie = avalonCookieRepository.findByToken(avalon);
        if (cookie.isEmpty()) {
            log.warn("유효하지 않은 avalon 토큰: {}", avalon);
            throw BusinessExceptionHandler.builder()
                .errorCode(ErrorCode.NOT_VALID_COOKIE_ERROR)
                .message("유효하지 않은 avalon 토큰입니다.")
                .build();
        }
        
        // 2. 프로젝트 키로 MariaDB에서 프로젝트 조회
        Integer projectKey = cookie.get().getProjectKey();
        ProjectEntity project = projectRepository.findById(projectKey)
            .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.PROJECT_NOT_FOUND));
        
        return convertToDetailedResponse(project, avalon);
    }
    

    @Transactional
    public DeleteProjectResponseDto deleteProject(String projectId) {
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
        return new DeleteProjectResponseDto();
    }

    // IF-PR-0005: 프로젝트 쿠키 삭제
    @Transactional
    public DeleteProjectCookieDto deleteProjectCookie(String avalon) {
        log.info("프로젝트 쿠키 삭제 요청. avalon: {}", avalon);
        if (avalon != null && !avalon.trim().isEmpty()) {
            avalonCookieRepository.deleteByToken(avalon);
            log.info("Redis 비활성화 상태 - 쿠키 삭제 요청만 처리");
        }
        return new DeleteProjectCookieDto();
    }

    @Transactional
    private void saveAvalonToRedis(String avalon, Integer projectKey) {
        avalonCookieRepository.save(AvalonCookieEntity.builder()
            .token(avalon)
            .projectKey(projectKey)
            .build());
    }
    

    private String generateUUID7Cookie() {
        return Generators.timeBasedReorderedGenerator().generate().toString();
    }
    
    private ProjectResponseDto convertToDetailedResponse(ProjectEntity project, String avalon) {
        String projectName = project.getId() + "_Project";
        
        List<String> specList = List.of("요구사항명세서", "인터페이스정의서", "인터페이스설계서");
    
        // OneToMany 관계 대신 Repository로 직접 조회
        List<RequestEntity> requests = requestRepository.findByProjectKey(project);
        List<RequirementInfoDto> requirements = requests.stream()
                .map(req -> new RequirementInfoDto(
                        req.getId(),
                        req.getName(),
                        req.getDescription(),
                        req.getPriorityKey() != null ? req.getPriorityKey().getName() : "중요도미정",
                        req.getMajorKey() != null ? req.getMajorKey().getName() : "대분류미정",
                        req.getMiddleKey() != null ? req.getMiddleKey().getName() : "중분류미정",
                        req.getMinorKey() != null ? req.getMinorKey().getName() : "소분류미정"))
                .collect(Collectors.toList());
    
        // OneToMany 관계 대신 Repository로 직접 조회
        List<ApiListEntity> apiLists = apiListRepository.findByProjectKey(project);
        List<ApiInfoDto> apiInfos = apiLists.stream()
                .map(this::convertApiEntityToDto)
                .collect(Collectors.toList());
    
        return new ProjectResponseDto(project.getId(), avalon,  // 파라미터로 받은 avalon 사용
                projectName, specList, requirements, apiInfos);
    }

    private ApiInfoDto convertApiEntityToDto(ApiListEntity api) {
        List<ParameterEntity> parameters = parameterRepository.findByApiListKey(api);
        
        List<ParameterDetailDto> pathQueryParams = parameters.stream()
            .filter(p -> p.getCategoryKey() != null && "PATH_QUERY".equals(p.getCategoryKey().getName()))
            .map(this::convertParameterEntityToDto).collect(Collectors.toList());

        List<ParameterDetailDto> requestParams = parameters.stream()
            .filter(p -> p.getCategoryKey() != null && "REQUEST".equals(p.getCategoryKey().getName()))
            .map(this::convertParameterEntityToDto).collect(Collectors.toList());

        List<ParameterDetailDto> responseParams = parameters.stream()
            .filter(p -> p.getCategoryKey() != null && "RESPONSE".equals(p.getCategoryKey().getName()))
            .map(this::convertParameterEntityToDto).collect(Collectors.toList());

        return ApiInfoDto.builder()
                .apiPk(api.getKey().longValue())
                .id(api.getId())
                .name(api.getName())
                .desc(api.getDescription())
                .method(api.getMethod())
                .url(api.getUrl())
                .path(api.getPath())
                .pathQuery(pathQueryParams)
                .request(requestParams)
                .response(responseParams)
                .build();
    }

    private ParameterDetailDto convertParameterEntityToDto(ParameterEntity param) {
        ParameterDetailDto.ParameterDetailDtoBuilder builder = ParameterDetailDto.builder()
                .id(param.getId())
                .korName(param.getNameKo())
                .name(param.getName())
                .itemType(param.getCategoryKey() != null ? param.getCategoryKey().getName() : null)
                .dataType(param.getDataType())
                .length(param.getLength())
                .format(param.getFormat())
                .defaultValue(param.getDefaultValue())
                .required(param.getRequired())
                .upper(param.getParentKey() != null ? param.getParentKey().getName() : null)
                .desc(param.getDescription());
        
        if (param.getApiListKey() != null) {
            builder.apiId(param.getApiListKey().getId());
            builder.apiName(param.getApiListKey().getName());
        }
        return builder.build();
    }

    private void processApiParameters(ApiListEntity apiList, ApiItem apiItem) {
        if (apiItem.getPathQuery() != null) {
            processParameterGroup(apiList, apiItem.getPathQuery(), "PATH_QUERY");
        }
        if (apiItem.getRequest() != null) {
            processParameterGroup(apiList, apiItem.getRequest(), "REQUEST");
        }
        if (apiItem.getResponse() != null) {
            processParameterGroup(apiList, apiItem.getResponse(), "RESPONSE");
        }
    }

    private void processParameterGroup(ApiListEntity apiList, ParameterGroup paramGroup, String groupType) {
        if (paramGroup == null) {
            return;
        }
        
        // getPq()가 ParameterItem를 반환하는지 확인 후 처리
        if (paramGroup.getPq() != null) {
            ParameterItem paramItem = convertToParameterItem(paramGroup.getPq());
            processParameterItems(apiList, List.of(paramItem), groupType);
        }
        
        // getReq()가 ParameterItem를 반환하는지 확인 후 처리  
        if (paramGroup.getReq() != null) {
            ParameterItem paramItem = convertToParameterItem(paramGroup.getReq());
            processParameterItems(apiList, List.of(paramItem), groupType);
        }
        
        // getRes()가 ParameterItem를 반환하는지 확인 후 처리
        if (paramGroup.getRes() != null) {
            ParameterItem paramItem = convertToParameterItem(paramGroup.getRes());
            processParameterItems(apiList, List.of(paramItem), groupType);
        }
    }

    private ParameterItem convertToParameterItem(ParameterItem item) {
        return ParameterItem.builder()
            .korName(item.getKorName())
            .name(item.getName())
            .itemType(item.getItemType())
            .dataType(item.getDataType())
            .format(item.getFormat())
            .defaultValue(item.getDefaultValue())
            .upper(item.getUpper())
            .desc(item.getDesc())
            .build();
    }

    @Transactional
    private void processParameterItems(ApiListEntity apiList, List<ParameterItem> paramItems, String paramType) {
        for (ParameterItem paramItem : paramItems) {
            String paramName = paramItem.getName() != null && !paramItem.getName().trim().isEmpty() 
                ? paramItem.getName() 
                : paramItem.getKorName();
                
            ParameterEntity.ParameterEntityBuilder builder = ParameterEntity.builder()
                .id(paramName + "_" + System.currentTimeMillis())  
                .nameKo(paramItem.getKorName())
                .name(paramItem.getName())
                .dataType(paramItem.getDataType())
                .format(paramItem.getFormat())
                .defaultValue(paramItem.getDefaultValue())
                .required(paramItem.isRequired())
                .description(paramItem.getDesc())
                .apiListKey(apiList)
                .categoryKey(findOrCreateCategory(paramType))
                .contextKey(findOrCreateContext(paramType));

            if (paramItem.getLength() > 0) {
                builder.length(paramItem.getLength());
            }

            parameterRepository.save(builder.build());
        }
    }

    @Transactional
    private CategoryEntity findOrCreateCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(CategoryEntity.builder().name(name).build()));
    }

    @Transactional
    private ContextEntity findOrCreateContext(String name) {
        return contextRepository.findByName(name)
                .orElseGet(() -> contextRepository.save(ContextEntity.builder().name(name).build()));
    }

    @Transactional
    private PriorityEntity findOrCreatePriority(String name) {
        PriorityEntity entity = priorityRepository.findByName(name).orElseGet(() -> {
            PriorityEntity newEntity = PriorityEntity.builder()
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();
            return priorityRepository.save(newEntity);
        });
        log.info("Priority 처리 완료 - name: {}, key: {}", entity.getName(), entity.getKey());
        return entity;
    }

    @Transactional
    private RequestMajorEntity findOrCreateRequestMajor(String name) {
        RequestMajorEntity entity = requestMajorRepository.findByName(name).orElseGet(() -> {
            RequestMajorEntity newEntity = RequestMajorEntity.builder()
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();
            return requestMajorRepository.save(newEntity);
        });
        log.info("RequestMajor 처리 완료 - name: {}, key: {}", entity.getName(), entity.getKey());
        return entity;
    }

    @Transactional
    private RequestMiddleEntity findOrCreateRequestMiddle(String name) {
        RequestMiddleEntity entity = requestMiddleRepository.findByName(name).orElseGet(() -> {
            RequestMiddleEntity newEntity = RequestMiddleEntity.builder()
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();
            return requestMiddleRepository.save(newEntity);
        });
        log.info("RequestMiddle 처리 완료 - name: {}, key: {}", entity.getName(), entity.getKey());
        return entity;
    }

    @Transactional
    private RequestMinorEntity findOrCreateRequestMinor(String name) {
        RequestMinorEntity entity = requestMinorRepository.findByName(name).orElseGet(() -> {
            RequestMinorEntity newEntity = RequestMinorEntity.builder()
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();
            return requestMinorRepository.save(newEntity);
        });
        log.info("RequestMinor 처리 완료 - name: {}, key: {}", entity.getName(), entity.getKey());
        return entity;
    }
}