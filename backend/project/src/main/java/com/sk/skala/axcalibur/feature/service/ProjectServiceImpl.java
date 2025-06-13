package com.sk.skala.axcalibur.feature.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.io.IOException;

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
import com.sk.skala.axcalibur.feature.entity.FilePathEntity;
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

    private static final String PARAM_TYPE_PATH_QUERY = "PATH_QUERY";
    private static final String PARAM_TYPE_REQUEST = "REQUEST";
    private static final String PARAM_TYPE_RESPONSE = "RESPONSE";

    
    // IF-PR-0001: 프로젝트 목록 저장

    @Transactional
    public SaveProjectResponseDto saveProject(String projectId, SaveProjectRequestDto request) {
        log.info("프로젝트 목록 저장 시작. projectId: {}", projectId);

        ProjectEntity project = projectRepository.findById(projectId)
            .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.PROJECT_NOT_FOUND));

        // 요구사항 데이터 저장
        if (request.getRequirement() != null && !request.getRequirement().isEmpty()) {
            for (ReqItem reqItem : request.getRequirement()) {

                RequestEntity.RequestEntityBuilder reqBuilder = RequestEntity.builder()
                    .id(reqItem.getId())
                    .name(reqItem.getName())
                    .description(reqItem.getDesc())
                    .projectKey(project);

                // 분류 정보 처리 (없으면 자동 생성)
                if (reqItem.getPriority() != null) {
                    PriorityEntity priority = findOrCreatePriority(reqItem.getPriority());
                    reqBuilder.priorityKey(priority);
                }
                if (reqItem.getMajor() != null) {
                    RequestMajorEntity major = findOrCreateRequestMajor(reqItem.getMajor());
                    reqBuilder.majorKey(major);
                }
                if (reqItem.getMiddle() != null) {
                    RequestMiddleEntity middle = findOrCreateRequestMiddle(reqItem.getMiddle());
                    reqBuilder.middleKey(middle);
                }
                if (reqItem.getMinor() != null) {
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
        
        // 1. Redis에서 avalon 토큰으로 프로젝트 조회
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
        
        ProjectEntity project = projectRepository.findById(projectId)
            .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.PROJECT_NOT_FOUND));
        
        Integer projectKey = project.getKey();
        
        // 1. 파일경로에 등록된 파일들 물리적 삭제
        List<FilePathEntity> filePaths = project.getFilePaths();
        for (FilePathEntity filePathEntity : filePaths) {
            java.nio.file.Path path = java.nio.file.Paths.get(filePathEntity.getPath());
            if (!java.nio.file.Files.exists(path)) {
                throw new BusinessExceptionHandler(ErrorCode.IO_ERROR);
            }
            try {
                java.nio.file.Files.delete(path);
                log.info("파일 삭제 완료: {}", filePathEntity.getPath());
            } catch (IOException e) {
                log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
                throw new BusinessExceptionHandler(ErrorCode.IO_ERROR);
            }
        }
        
        // 2. Redis에서 해당 프로젝트 쿠키들 삭제
        List<AvalonCookieEntity> cookies = new ArrayList<>();
        avalonCookieRepository.findAll().forEach(cookies::add);
        avalonCookieRepository.deleteAll(
            cookies.stream()
                .filter(cookie -> projectKey.equals(cookie.getProjectKey()))
                .collect(Collectors.toList())
        );
        
        // 3. 프로젝트 삭제 (Cascade로 관련 데이터 자동 삭제)
        projectRepository.delete(project);
        
        log.info("프로젝트 삭제 완료");
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
        return new DeleteProjectCookieDto(LocalDateTime.now().toString(), "");
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
        return Generators.timeBasedReorderedGenerator().generate().toString().replace("-", "");
    }

    // 프로젝트 엔티티를 상세 응답 DTO로 변환
     
    private ProjectResponseDto convertToDetailedResponse(ProjectEntity project, String avalon) {
        
        // 요구사항 정보 조회 및 변환
        List<RequestEntity> requests = requestRepository.findByProjectKey(project);
        List<RequirementInfoDto> requirements = requests.stream()
                .map(req -> new RequirementInfoDto(
                        req.getId(),
                        req.getName(),
                        req.getDescription(),
                        req.getPriorityKey().getName(),
                        req.getMajorKey().getName(),
                        req.getMiddleKey().getName(),
                        req.getMinorKey().getName()))
                .collect(Collectors.toList());
    
        // API 목록 정보 조회 및 변환
        List<ApiListEntity> apiLists = apiListRepository.findByProjectKey(project);
        List<ApiInfoDto> apiInfos = apiLists.stream()
                .map(this::convertApiEntityToDto)
                .collect(Collectors.toList());
    
        return new ProjectResponseDto(project.getId(), avalon,
                project.getId(), requirements, apiInfos);
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
                .upper(param.getParentKey() != null ? param.getParentKey().getKey() : null)
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
            processParameterGroup(apiList, apiItem.getPathQuery(), PARAM_TYPE_PATH_QUERY);
        }
        if (apiItem.getRequest() != null) {
            processParameterGroup(apiList, apiItem.getRequest(), PARAM_TYPE_REQUEST);
        }
        if (apiItem.getResponse() != null) {
            processParameterGroup(apiList, apiItem.getResponse(), PARAM_TYPE_RESPONSE);
        }
    }

    /**
     * 파라미터 그룹 내의 개별 파라미터들을 처리
     */
    private void processParameterGroup(ApiListEntity apiList, ParameterGroup paramGroup, String groupType) {
        if (paramGroup == null) {
            log.debug("파라미터 그룹이 null입니다. groupType: {}", groupType);
            return;
        }

        List<ParameterItem> targetParams = getTargetParameters(paramGroup, groupType);
        if (targetParams != null && !targetParams.isEmpty()) {
            log.debug("파라미터 처리 시작 - groupType: {}, 파라미터 수: {}", groupType, targetParams.size());
            processParameterItems(apiList, targetParams, groupType);
        } else {
            log.debug("처리할 파라미터가 없습니다. groupType: {}", groupType);
        }
    }

    /**
     * 그룹 타입에 해당하는 파라미터 목록을 반환
     */
    private List<ParameterItem> getTargetParameters(ParameterGroup paramGroup, String groupType) {
        switch (groupType) {
            case PARAM_TYPE_PATH_QUERY:
                return paramGroup.getPq();
            case PARAM_TYPE_REQUEST:
                return paramGroup.getReq();
            case PARAM_TYPE_RESPONSE:
                return paramGroup.getRes();
            default:
                log.warn("지원하지 않는 파라미터 그룹 타입입니다: {}", groupType);
                return null;
        }
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

            processParameterItem(paramItem, apiList, builder);
            
            parameterRepository.save(builder.build());
        }
    }

    
    // 카테고리 조회 또는 생성 (PATH_QUERY, REQUEST, RESPONSE)
     
    @Transactional
    private CategoryEntity findOrCreateCategory(String name) {
        return categoryRepository.findByName(name)
            .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NOT_FOUND_ERROR));
    }

    // 컨텍스트 조회 또는 생성
     
    @Transactional
    private ContextEntity findOrCreateContext(String name) {
        return contextRepository.findByName(name)
            .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NOT_FOUND_ERROR));
    }

    // 부모 파라미터 찾기 (Self-Join을 위한 부모 참조 검색)
     
    @Transactional
    private ParameterEntity findParentParameter(Integer parentKey) {
        if (parentKey == null) return null;
        return parameterRepository.findById(parentKey).orElse(null);
    }

    // 우선순위 엔티티 조회 또는 생성
     
    @Transactional
    private PriorityEntity findOrCreatePriority(String name) {
        return priorityRepository.findByName(name)
            .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NOT_FOUND_ERROR));
    }

    
    // 요구사항 대분류 엔티티 조회 또는 생성
     
    @Transactional
    private RequestMajorEntity findOrCreateRequestMajor(String name) {
        RequestMajorEntity entity = requestMajorRepository.findByName(name).orElseGet(() -> {
            RequestMajorEntity newEntity = RequestMajorEntity.builder()
                .name(name)
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

    private void processParameterItem(ParameterItem paramItem, ApiListEntity apiList, ParameterEntity.ParameterEntityBuilder builder) {
        if (paramItem.getUpper() != null) {
            // 부모 파라미터 찾아서 설정
            ParameterEntity parent = findParentParameter(paramItem.getUpper());
            if (parent == null) {
                log.error("부모 파라미터를 찾을 수 없습니다. upper: {}", paramItem.getUpper());
                throw new BusinessExceptionHandler(ErrorCode.NOT_FOUND_ERROR);
            }
            builder.parentKey(parent);
        }
    }
}