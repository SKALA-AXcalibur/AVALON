package com.sk.skala.axcalibur.spec.feature.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.uuid.Generators;
import com.sk.skala.axcalibur.spec.feature.project.dto.ApiInfoDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.CreateProjectRequestDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.CreateProjectResponseDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.DeleteProjectCookieDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.DeleteProjectResponseDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.ParameterDetailDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.ProjectResponseDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.RequirementInfoDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.SaveProjectRequestDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.SaveProjectResponseDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.TableInfoDto;
import com.sk.skala.axcalibur.spec.feature.project.dto.item.ApiItem;
import com.sk.skala.axcalibur.spec.feature.project.dto.item.ColItem;
import com.sk.skala.axcalibur.spec.feature.project.dto.item.ParameterItem;
import com.sk.skala.axcalibur.spec.feature.project.dto.item.ReqItem;
import com.sk.skala.axcalibur.spec.feature.project.dto.item.TableItem;
import com.sk.skala.axcalibur.spec.global.entity.ProjectEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.RequestEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.ApiListEntity;
import com.sk.skala.axcalibur.spec.global.entity.AvalonCookieEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.ParameterEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.CategoryEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.ContextEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.DbColumnEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.DbDesignEntity;
import com.sk.skala.axcalibur.spec.global.entity.FilePathEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.PriorityEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.RequestMajorEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.RequestMiddleEntity;
import com.sk.skala.axcalibur.spec.feature.project.entity.RequestMinorEntity;
import com.sk.skala.axcalibur.spec.feature.project.repository.ApiListRepository;
import com.sk.skala.axcalibur.spec.feature.project.repository.CategoryRepository;
import com.sk.skala.axcalibur.spec.feature.project.repository.ContextRepository;
import com.sk.skala.axcalibur.spec.feature.project.repository.DbColumnRepository;
import com.sk.skala.axcalibur.spec.feature.project.repository.DbDesignRepository;
import com.sk.skala.axcalibur.spec.feature.project.repository.ParameterRepository;
import com.sk.skala.axcalibur.spec.feature.project.repository.PriorityRepository;
import com.sk.skala.axcalibur.spec.feature.project.repository.RequestMajorRepository;
import com.sk.skala.axcalibur.spec.feature.project.repository.RequestMiddleRepository;
import com.sk.skala.axcalibur.spec.feature.project.repository.RequestMinorRepository;
import com.sk.skala.axcalibur.spec.feature.project.repository.RequestRepository;
import com.sk.skala.axcalibur.spec.global.repository.ProjectRepository;
import com.sk.skala.axcalibur.spec.global.repository.AvalonCookieRepository;
import com.sk.skala.axcalibur.spec.global.code.ErrorCode;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final DbColumnRepository dbColumnRepository;

    private final DbDesignRepository dbDesignRepository;

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
    public SaveProjectResponseDto saveProject(String projectId, SaveProjectRequestDto request) {
        log.info("프로젝트 목록 저장 시작. projectId: {}", projectId);

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.PROJECT_NOT_FOUND));

        List<RequestEntity> existingRequests = requestRepository.findByProjectKey(project);
        requestRepository.deleteAll(existingRequests);
        
        List<ApiListEntity> existingApis = apiListRepository.findByProjectKey(project);
        apiListRepository.deleteAll(existingApis);
        
        List<DbDesignEntity> existingTables = dbDesignRepository.findByProjectKey(project);
        dbDesignRepository.deleteAll(existingTables);

        // 요구사항 데이터 저장
        if (request.getRequirement() != null && !request.getRequirement().isEmpty()) {
            List<RequestEntity> requestEntities = request.getRequirement().stream()
                .map((ReqItem reqItem) -> {
                    RequestEntity.RequestEntityBuilder reqBuilder = RequestEntity.builder()
                            .id(reqItem.getId())
                            .name(reqItem.getName())
                            .description(reqItem.getDesc())
                            .projectKey(project);

                    // 분류 정보 처리 (없으면 자동 생성)
                    if (reqItem.getPriority() != null) {
                        PriorityEntity priority = findAndCreatePriority(reqItem.getPriority());
                        reqBuilder.priorityKey(priority);
                    }
                    if (reqItem.getMajor() != null) {
                        RequestMajorEntity major = findAndCreateRequestMajor(reqItem.getMajor());
                        reqBuilder.majorKey(major);
                    }
                    if (reqItem.getMiddle() != null) {
                        RequestMiddleEntity middle = findAndCreateRequestMiddle(reqItem.getMiddle());
                        reqBuilder.middleKey(middle);
                    }
                    if (reqItem.getMinor() != null) {
                        RequestMinorEntity minor = findAndCreateRequestMinor(reqItem.getMinor());
                        reqBuilder.minorKey(minor);
                    }

                    RequestEntity reqEntity = reqBuilder.build();

                    log.info(" Request 저장 전 확인 - majorKey: {}, middleKey: {}, minorKey: {}, priorityKey: {}",
                            reqEntity.getMajorKey() != null ? reqEntity.getMajorKey().getKey() : "null",
                            reqEntity.getMiddleKey() != null ? reqEntity.getMiddleKey().getKey() : "null",
                            reqEntity.getMinorKey() != null ? reqEntity.getMinorKey().getKey() : "null",
                            reqEntity.getPriorityKey() != null ? reqEntity.getPriorityKey().getKey() : "null");
                    
                    return reqEntity;
                })
                .collect(Collectors.toList());
            
            // 배치 저장
            requestRepository.saveAll(requestEntities);
        }

        // API 목록 데이터 저장
        if (request.getApiList() != null && !request.getApiList().isEmpty()) {
            // reqId 수집
            List<String> reqIds = request.getApiList().stream()
                .map(ApiItem::getReqId)
                .distinct()
                .collect(Collectors.toList());

            // DB에서 한 번에 조회 (프로젝트별 필터링 적용)
            List<RequestEntity> allProjectRequests = requestRepository.findByProjectKey(project);
            List<RequestEntity> requestEntities = allProjectRequests.stream()
                .filter(req -> reqIds.contains(req.getId()))
                .collect(Collectors.toList());

            // Map 생성
            Map<String, RequestEntity> requestMap = requestEntities.stream()
                .collect(Collectors.toMap(RequestEntity::getId, Function.identity()));
        
            // API 엔티티 생성
            List<ApiListEntity> apiEntities = request.getApiList().stream()
                .map(apiItem -> {
                    RequestEntity linkedRequest = requestMap.get(apiItem.getReqId());
                    if(linkedRequest == null) {
                        throw new BusinessExceptionHandler(ErrorCode.NOT_FOUND_ERROR);
                    }
                    return ApiListEntity.builder()
                            .id(apiItem.getId())
                            .name(apiItem.getName())
                            .description(apiItem.getDesc())
                            .method(apiItem.getMethod())
                            .url(apiItem.getUrl())
                            .path(apiItem.getPath())
                            .projectKey(project)
                            .requestKey(linkedRequest)
                            .build();
                })
                .collect(Collectors.toList());
            
            // API 엔티티들 배치 저장
            List<ApiListEntity> savedApiEntities = apiListRepository.saveAll(apiEntities);
            
            // 파라미터 처리 - 배치 처리로 개선
            List<ParameterEntity> allParameters = new ArrayList<>();
            
            for (int i = 0; i < request.getApiList().size(); i++) {
                ApiItem apiItem = request.getApiList().get(i);
                ApiListEntity savedApiEntity = savedApiEntities.get(i);
                
                // PATH/QUERY 파라미터들 수집
                if (apiItem.getPathQuery() != null && !apiItem.getPathQuery().isEmpty()) {
                    allParameters.addAll(createParameterEntitiesFromItems(savedApiEntity, apiItem.getPathQuery(), "PATH/QUERY"));
                }
                
                // REQUEST 파라미터들 수집
                if (apiItem.getRequest() != null && !apiItem.getRequest().isEmpty()) {
                    allParameters.addAll(createParameterEntitiesFromItems(savedApiEntity, apiItem.getRequest(), "REQUEST"));
                }
                
                // RESPONSE 파라미터들 수집
                if (apiItem.getResponse() != null && !apiItem.getResponse().isEmpty()) {
                    allParameters.addAll(createParameterEntitiesFromItems(savedApiEntity, apiItem.getResponse(), "RESPONSE"));
                }
            }
            
            // 모든 파라미터를 한 번에 배치 저장
            if (!allParameters.isEmpty()) {
                    List<ParameterEntity> savedParameters = parameterRepository.saveAll(allParameters);
                    updateParentKeys(savedParameters);   // ➕ 추가
            }
        }
 
        // 테이블 목록 데이터 저장
        if (request.getTableList() != null && !request.getTableList().isEmpty()) {
            // 테이블 엔티티들을 배치로 생성
            List<DbDesignEntity> tableEntities = request.getTableList().stream()
                .map((TableItem tableItem) -> DbDesignEntity.builder()
                        .name(tableItem.getName())
                        .projectKey(project)
                        .build())
                .collect(Collectors.toList());
            
            // 테이블 배치 저장
            List<DbDesignEntity> savedTables = dbDesignRepository.saveAll(tableEntities);
            
            // 컬럼들을 평면화하여 배치 처리
            List<DbColumnEntity> allColumns = request.getTableList().stream()
                .flatMap((TableItem tableItem) -> {
                    int tableIndex = request.getTableList().indexOf(tableItem);
                    return tableItem.getColumn().stream()
                        .map(colItem -> DbColumnEntity.builder()
                                .colName(colItem.getColName())
                                .description(colItem.getDesc())
                                .type(colItem.getType())
                                .length(colItem.getLength())
                                .isPk(colItem.getIsPk())
                                .fk(colItem.getFk())
                                .isNull(colItem.getIsNull())
                                .constraint(colItem.getConstraint())
                                .dbDesignKey(savedTables.get(tableIndex))
                                .build());
                })
                .collect(Collectors.toList());
            
            // 컬럼 배치 저장
            dbColumnRepository.saveAll(allColumns);
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

        // 테이블 설계 정보 조회 및 변환
        List<DbDesignEntity> dbDesigns = dbDesignRepository.findByProjectKey(project);
        List<TableInfoDto> tableList = dbDesigns.stream()
                .<TableInfoDto>map(dbDesign -> TableInfoDto.builder()
                        .name(dbDesign.getName())
                        .column(dbDesign.getColumns().stream()
                                .<ColItem>map(col -> ColItem.builder()
                                        .colName(col.getColName())
                                        .desc(col.getDescription())
                                        .type(col.getType())
                                        .length(col.getLength())
                                        .isPk(col.getIsPk())
                                        .fk(col.getFk())
                                        .isNull(col.getIsNull())
                                        .constraint(col.getConstraint())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
        
        // 요구사항 정보 조회 및 변환 (기존 convertToDetailedResponse의 로직)
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

        // API 목록 정보 조회 및 변환 (기존 convertToDetailedResponse의 로직)
        List<ApiListEntity> apiLists = apiListRepository.findByProjectKey(project);
        List<ApiInfoDto> apiInfos = apiLists.stream()
                .map(this::convertApiEntityToDto)
                .collect(Collectors.toList());

        // 최종 응답 DTO 생성
        return ProjectResponseDto.builder()
                .projectId(project.getId())
                .avalon(avalon)
                .tableList(tableList)
                .requirement(requirements)
                .apiList(apiInfos)
                .build();
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

            if (java.nio.file.Files.exists(path)) {
                try {
                    java.nio.file.Files.delete(path);
                    log.info("파일 삭제 완료: {}", filePathEntity.getPath());
                } catch (IOException e) {
                    log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
                    throw new BusinessExceptionHandler(ErrorCode.IO_ERROR);
                }
            }
        }

        // 2. Redis에서 해당 프로젝트 쿠키들 삭제
        List<AvalonCookieEntity> cookies = avalonCookieRepository.findAllByProjectKey(projectKey);
        log.info("cookies: {}", cookies);
        avalonCookieRepository.deleteAll(cookies);

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

    // API 엔티티를 DTO로 변환 (파라미터 계층 구조 포함)

    private ApiInfoDto convertApiEntityToDto(ApiListEntity api) {
        // JOIN FETCH로 파라미터와 부모를 한 번에 조회
        List<ParameterEntity> parameters = parameterRepository.findByApiListKeyWithParent(api);

        // 파라미터 타입별로 분류하여 변환
        List<ParameterDetailDto> pathQueryParams = parameters.stream()
                .filter(p -> p.getCategoryKey() != null && "PATH/QUERY".equals(p.getCategoryKey().getName()))
                .map(this::convertParameterEntityToDto).collect(Collectors.toList());

        List<ParameterDetailDto> requestParams = parameters.stream()
                .filter(p -> p.getCategoryKey() != null && "REQUEST".equals(p.getCategoryKey().getName()))
                .map(this::convertParameterEntityToDto).collect(Collectors.toList());

        List<ParameterDetailDto> responseParams = parameters.stream()
                .filter(p -> p.getCategoryKey() != null && "RESPONSE".equals(p.getCategoryKey().getName()))
                .map(this::convertParameterEntityToDto).collect(Collectors.toList());

        return ApiInfoDto.builder()
                .apiPk(api.getKey())
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
                .nameKo(param.getNameKo())
                .name(param.getName())
                .itemType(param.getCategoryKey() != null ? param.getCategoryKey().getName() : null)
                .dataType(param.getDataType())
                .length(param.getLength())
                .format(param.getFormat())
                .defaultValue(param.getDefaultValue())
                .required(param.getRequired())
                // JOIN FETCH로 가져왔기 때문에 추가 쿼리 없이 부모 정보 사용 가능
                .upper(param.getParentKey() != null ? param.getParentKey().getKey() : null)
                .desc(param.getDescription());

        if (param.getApiListKey() != null) {
            builder.apiId(param.getApiListKey().getId());
            builder.apiName(param.getApiListKey().getName());
        }
        return builder.build();
    }

    // 파라미터 목록을 DB에 저장 (Self-Join 구조 처리)

    @Transactional
    private void processParameterItems(ApiListEntity apiList, List<ParameterItem> paramItems, String paramType) {
        List<ParameterEntity> parameterEntities = createParameterEntitiesFromItems(apiList, paramItems, paramType);
        // 배치 저장
        parameterRepository.saveAll(parameterEntities);
    }

    // 파라미터 엔티티들을 생성하는 메서드 (저장하지 않고 엔티티만 생성)
    private List<ParameterEntity> createParameterEntitiesFromItems(ApiListEntity apiList, List<ParameterItem> paramItems, String paramType) {
        // 카테고리와 컨텍스트를 미리 조회
        CategoryEntity category = findCategory(paramType);
        
        return paramItems.stream()
            .filter(paramItem -> !isEmptyParameter(paramItem))
            .map(paramItem -> {
                log.info("파라미터 엔티티 생성 중 - itemType: {}", paramItem.getItemType());

                ParameterEntity.ParameterEntityBuilder builder = ParameterEntity.builder()
                        .nameKo(paramItem.getNameKo())
                        .name(paramItem.getName())
                        .dataType(paramItem.getDataType())
                        .format(paramItem.getFormat())
                        .defaultValue(paramItem.getDefaultValue())
                        .required(paramItem.getRequired())
                        .description(paramItem.getDesc())
                        .apiListKey(apiList)
                        .categoryKey(category)
                        .contextKey(findContext(paramItem.getItemType()))
                        .upperName(paramItem.getUpper()); // 상위항목명 조회

                if (paramItem.getLength() != null && paramItem.getLength() > 0) {
                    builder.length(paramItem.getLength());
                }

                return builder.build();
            })
            .collect(Collectors.toList());
    }

    // 상위항목 키 조회
    private void updateParentKeys(List<ParameterEntity> savedParameters) {
        // API 별로 파라미터들을 그룹화
        Map<ApiListEntity, List<ParameterEntity>> apiToParamsMap = savedParameters.stream()
                .collect(Collectors.groupingBy(ParameterEntity::getApiListKey));

        for (Map.Entry<ApiListEntity, List<ParameterEntity>> entry : apiToParamsMap.entrySet()) {
            List<ParameterEntity> paramList = entry.getValue();

            // 같은 API 내 name → ParameterEntity Map 생성
            Map<String, ParameterEntity> nameToEntity = paramList.stream()
                    .collect(Collectors.toMap(ParameterEntity::getName, Function.identity(), (a, b) -> a));  // 중복 시 첫 번째

            List<ParameterEntity> toUpdate = new ArrayList<>();

            for (ParameterEntity param : paramList) {
                String upperName = param.getUpperName();

                if (upperName != null && nameToEntity.containsKey(upperName)) {
                    ParameterEntity parent = nameToEntity.get(upperName);

                    // 순환 참조 방지: 자기 자신을 부모로 참조 금지
                    if (!param.equals(parent)) {
                        param.setParentKey(parent);
                        toUpdate.add(param);
                    } else {
                        log.warn("자기 자신을 부모로 설정하려는 순환 참조 감지: paramName={}", param.getName());
                    }
                }
            }

            if (!toUpdate.isEmpty()) {
                parameterRepository.saveAll(toUpdate);
            }
        }
    }

    // 카테고리 조회

    @Transactional
    private CategoryEntity findCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NOT_FOUND_ERROR));
    }

    // 컨텍스트 조회

    @Transactional
    private ContextEntity findContext(String name) {
        log.info("findContext 호출됨 - name: {}", name);
        return contextRepository.findByName(name)
                .orElseThrow(() -> {
                    log.error("Context를 찾을 수 없습니다. name: {}", name);
                    return new BusinessExceptionHandler(ErrorCode.NOT_FOUND_ERROR);
                });
    }

    // 부모 파라미터 찾기 (Self-Join을 위한 부모 참조 검색)

    @Transactional
    private ParameterEntity findParentParameter(Integer parentKey) {
        if (parentKey == null)
            return null;
        return parameterRepository.findById(parentKey).orElse(null);
    }

    // 요구사항 대분류 엔티티 조회 및 생성
    @Transactional
    private RequestMajorEntity findAndCreateRequestMajor(String name) {
        return requestMajorRepository.findByName(name)
            .orElseGet(() -> {
                RequestMajorEntity entity = RequestMajorEntity.builder()
                    .name(name)
                    .build();
                return requestMajorRepository.save(entity);
            });
    }

    // 요구사항 중분류 엔티티 조회 및 생성
    @Transactional
    private RequestMiddleEntity findAndCreateRequestMiddle(String name) {
        return requestMiddleRepository.findByName(name)
            .orElseGet(() -> {
                RequestMiddleEntity entity = RequestMiddleEntity.builder()
                    .name(name)
                    .build();
                return requestMiddleRepository.save(entity);
            });
    }

    // 요구사항 소분류 엔티티 조회 및 생성
    @Transactional
    private RequestMinorEntity findAndCreateRequestMinor(String name) {
        return requestMinorRepository.findByName(name)
            .orElseGet(() -> {
                RequestMinorEntity entity = RequestMinorEntity.builder()
                    .name(name)
                    .build();
                return requestMinorRepository.save(entity);
            });
    }

    // 우선순위 엔티티 조회 및 생성
    @Transactional
    private PriorityEntity findAndCreatePriority(String name) {
        return priorityRepository.findByName(name)
            .orElseGet(() -> {
                PriorityEntity entity = PriorityEntity.builder()
                    .name(name)
                    .build();
                return priorityRepository.save(entity);
            });
    }

    // 빈 파라미터 체크
    private boolean isEmptyParameter(ParameterItem paramItem) {
        // 이름과 한글명이 모두 비어있으면 의미없는 데이터로 판단
        boolean hasNoName = (paramItem.getName() == null || paramItem.getName().trim().isEmpty()) &&
                (paramItem.getNameKo() == null || paramItem.getNameKo().trim().isEmpty());

        // 데이터 타입도 비어있으면 완전히 빈 데이터
        boolean hasNoDataType = (paramItem.getDataType() == null || paramItem.getDataType().trim().isEmpty());

        return hasNoName && hasNoDataType;
    }
}