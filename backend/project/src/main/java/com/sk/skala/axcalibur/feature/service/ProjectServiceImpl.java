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

    
    // IF-PR-0001: 프로젝트 목록 저장

    @Transactional
    public SaveProjectResponseDto saveProject(String projectId, SaveProjectRequestDto request, String avalon) {
        log.info("프로젝트 목록 저장 시작. projectId: {}", projectId);

        AvalonCookieEntity cookie = avalonCookieRepository.findByToken(avalon)
            .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NOT_VALID_COOKIE_ERROR));

        ProjectEntity tokenProject = projectRepository.findById(cookie.getProjectKey())
            .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.PROJECT_NOT_FOUND));

        if (!projectId.equals(tokenProject.getId())) {
            log.error("프로젝트 ID 불일치 projectId: {}, tokenProjectId: {}", projectId, tokenProject.getId());
            throw new BusinessExceptionHandler(ErrorCode.NOT_VALID_ERROR);
        }

        

        ProjectEntity project = projectRepository.findById(projectId).orElseGet(() -> { 
            ProjectEntity newProject = ProjectEntity.builder()
                .id(projectId)
                .build();
            log.info("새 프로젝트 생성. projectId: {}", projectId);
            return projectRepository.save(newProject);
        });

        // 요구사항 데이터 저장
        if (request.getRequirement() != null && !request.getRequirement().isEmpty()) {
            for (ReqItem reqItem : request.getRequirement()) {
                // ID 필수 검증
                if (reqItem.getId() == null || reqItem.getId().trim().isEmpty()) {
                    throw new BusinessExceptionHandler(ErrorCode.NOT_VALID_ERROR);
                }

                RequestEntity.RequestEntityBuilder reqBuilder = RequestEntity.builder()
                    .id(reqItem.getId())
                    .name(reqItem.getName())
                    .description(reqItem.getDesc())
                    .projectKey(project);

                // 분류 정보 처리 (없으면 자동 생성)
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

        // API 목록 데이터 저장
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

    
    // IF-PR-0002: 프로젝트 상세 조회

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
    
    // IF-PR-0003: 프로젝트 정보 삭제

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

    
    // IF-PR-0004: 프로젝트 생성

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
        projectRepository.save(project);
        log.info("Avalon 토큰 생성/업데이트. projectId: {}, newAvalon: {}", projectId, avalon);

        saveAvalonToRedis(avalon, project.getKey()); 
        return new CreateProjectResponseDto(projectId, avalon);
    }

    
    // IF-PR-0005: 프로젝트 쿠키 삭제

    @Transactional
    public DeleteProjectCookieDto deleteProjectCookie(String avalon) {
        log.info("프로젝트 쿠키 삭제 요청. avalon: {}", avalon);
        if (avalon != null && !avalon.trim().isEmpty()) {
            avalonCookieRepository.deleteByToken(avalon);
            log.info("쿠키 삭제 처리");
        }
        return new DeleteProjectCookieDto();
    }

    // ========================================
    // 내부 유틸리티 메서드들
    // ========================================

    // avalon 토큰을 Redis에 저장
     
    @Transactional
    private void saveAvalonToRedis(String avalon, Integer projectKey) {
        avalonCookieRepository.save(AvalonCookieEntity.builder()
            .token(avalon)
            .projectKey(projectKey)
            .build());
    }

    // UUID7 기반 쿠키 생성
    private String generateUUID7Cookie() {
        return Generators.timeBasedReorderedGenerator().generate().toString();
    }

    // 프로젝트 엔티티를 상세 응답 DTO로 변환
     
    private ProjectResponseDto convertToDetailedResponse(ProjectEntity project, String avalon) {
        String projectName = project.getId() + "_Project";
        
        List<String> specList = List.of("요구사항명세서", "인터페이스정의서", "인터페이스설계서");
    
        // 요구사항 정보 조회 및 변환
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
    
        // API 목록 정보 조회 및 변환
        List<ApiListEntity> apiLists = apiListRepository.findByProjectKey(project);
        List<ApiInfoDto> apiInfos = apiLists.stream()
                .map(this::convertApiEntityToDto)
                .collect(Collectors.toList());
    
        return new ProjectResponseDto(project.getId(), avalon,
                projectName, specList, requirements, apiInfos);
    }

    // API 엔티티를 DTO로 변환 (파라미터 계층 구조 포함)
     
    private ApiInfoDto convertApiEntityToDto(ApiListEntity api) {
        List<ParameterEntity> parameters = parameterRepository.findByApiListKey(api);
        
        // 파라미터 타입별로 분류하여 변환
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

    // 파라미터 엔티티를 DTO로 변환 (Self-Join 관계 처리)
     
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

    // API 아이템의 파라미터 그룹들을 처리
     
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

    /**
     * 파라미터 그룹 내의 개별 파라미터들을 처리
     */
    private void processParameterGroup(ApiListEntity apiList, ParameterGroup paramGroup, String groupType) {
        if (paramGroup == null) {
            return;
        }
        
        // PATH_QUERY 파라미터 처리
        if (paramGroup.getPq() != null) {
            ParameterItem paramItem = convertToParameterItem(paramGroup.getPq());
            processParameterItems(apiList, List.of(paramItem), groupType);
        }
        
        // REQUEST 파라미터 처리
        if (paramGroup.getReq() != null) {
            ParameterItem paramItem = convertToParameterItem(paramGroup.getReq());
            processParameterItems(apiList, List.of(paramItem), groupType);
        }
        
        // RESPONSE 파라미터 처리
        if (paramGroup.getRes() != null) {
            ParameterItem paramItem = convertToParameterItem(paramGroup.getRes());
            processParameterItems(apiList, List.of(paramItem), groupType);
        }
    }

    // ParameterItem 객체 복사 (필드 선택적 처리용)
     
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

    // 파라미터 목록을 DB에 저장 (Self-Join 구조 처리)
     
    @Transactional
    private void processParameterItems(ApiListEntity apiList, List<ParameterItem> paramItems, String paramType) {
        for (ParameterItem paramItem : paramItems) {

            if (isEmptyParameter(paramItem)) {
                log.debug("빈 파라미터 데이터 스킵 - name: {}, korName: {}, dataType: {}", 
                    paramItem.getName(), paramItem.getKorName(), paramItem.getDataType());
                continue;
            }

            String paramName = paramItem.getName() != null && !paramItem.getName().trim().isEmpty() 
                ? paramItem.getName() 
                : paramItem.getKorName();
            // ID 생성: 파라미터명 8자 + UUID 8자 = 최대 17자
            String safeId = (paramName != null ? paramName.substring(0, Math.min(paramName.length(), 8)) : "param") 
                + "_" + java.util.UUID.randomUUID().toString().substring(0, 8);

            ParameterEntity.ParameterEntityBuilder builder = ParameterEntity.builder()
                .id(safeId)     
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

            // Self-Join 관계 처리
            if (paramItem.getUpper() != null && !paramItem.getUpper().trim().isEmpty()) {
                // 부모 파라미터 찾아서 설정
                ParameterEntity parent = findParentParameter(paramItem.getUpper(), apiList);
                if (parent == null) {
                    log.error("부모 파라미터를 찾을 수 없습니다. upper: {}", paramItem.getUpper());
                    throw new BusinessExceptionHandler(ErrorCode.NOT_FOUND_ERROR);
                }
                builder.parentKey(parent);
            }        
            // 최상위 파라미터는 parentKey가 null로 자동 설정됨
            
            parameterRepository.save(builder.build());
        }
    }

    
    // 카테고리 조회 또는 생성 (PATH_QUERY, REQUEST, RESPONSE)
     
    @Transactional
    private CategoryEntity findOrCreateCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(CategoryEntity.builder().name(name).build()));
    }

    // 컨텍스트 조회 또는 생성
     
    @Transactional
    private ContextEntity findOrCreateContext(String name) {
        return contextRepository.findByName(name)
                .orElseGet(() -> contextRepository.save(ContextEntity.builder().name(name).build()));
    }

    // 부모 파라미터 찾기 (Self-Join을 위한 부모 참조 검색)
     
    @Transactional
    private ParameterEntity findParentParameter(String parentName, ApiListEntity apiList) {
        // 같은 API 내에서 부모 파라미터 찾기
        List<ParameterEntity> existingParams = parameterRepository.findByApiListKey(apiList);
        
        return existingParams.stream()
            .filter(param -> parentName.equals(param.getName()) || parentName.equals(param.getNameKo()))
            .findFirst()
            .orElse(null); // 부모를 찾을 수 없으면 null 반환
    }

    // 우선순위 엔티티 조회 또는 생성
     
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

    
    // 요구사항 대분류 엔티티 조회 또는 생성
     
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

    
    // 요구사항 중분류 엔티티 조회 또는 생성
     
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

    
    // 요구사항 소분류 엔티티 조회 또는 생성
     
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

    // 빈 파라미터 체크
    private boolean isEmptyParameter(ParameterItem paramItem) {
        // 이름과 한글명이 모두 비어있으면 의미없는 데이터로 판단
        boolean hasNoName = (paramItem.getName() == null || paramItem.getName().trim().isEmpty()) &&
                            (paramItem.getKorName() == null || paramItem.getKorName().trim().isEmpty());
        
        // 데이터 타입도 비어있으면 완전히 빈 데이터
        boolean hasNoDataType = (paramItem.getDataType() == null || paramItem.getDataType().trim().isEmpty());
        
        return hasNoName && hasNoDataType;
    }
}