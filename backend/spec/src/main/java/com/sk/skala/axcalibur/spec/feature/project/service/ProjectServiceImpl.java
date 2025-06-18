package com.sk.skala.axcalibur.spec.feature.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
                    apiListRepository.save(apiEntity);
            }
        }

        // 테이블 목록 데이터 저장
        if (request.getTableList() != null && !request.getTableList().isEmpty()) {
            for (TableItem tableItem : request.getTableList()) {
                // 테이블 목록 저장
                DbDesignEntity dbDesignEntity = DbDesignEntity.builder()
                        .name(tableItem.getName())
                        .projectKey(project)
                        .build();
                DbDesignEntity dbDesign = dbDesignRepository.save(dbDesignEntity);

                // 컬럼 목록 저장
                if (tableItem.getColumn() != null) {
                    for (ColItem colItem : tableItem.getColumn()) {
                        DbColumnEntity dbColumnEntity = DbColumnEntity.builder()
                                .colName(colItem.getColName())
                                .description(colItem.getDesc())
                                .type(colItem.getType())
                                .length(colItem.getLength())
                                .isPk(colItem.getIsPk())
                                .fk(colItem.getFk())
                                .isNull(colItem.getIsNull())
                                .constraint(colItem.getConstraint())
                                .dbDesignKey(dbDesign)
                                .build();
                        dbColumnRepository.save(dbColumnEntity);
                    }
                }
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

        // 기존 응답 DTO에 테이블 리스트 추가
        return ProjectResponseDto.builder()
                .projectId(project.getId())
                .avalon(avalon)
                .tableList(tableList) // 테이블 리스트 추가
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
        List<AvalonCookieEntity> cookies = new ArrayList<>();
        avalonCookieRepository.findAll().forEach(cookies::add);
        avalonCookieRepository.deleteAll(
                cookies.stream()
                        .filter(cookie -> projectKey.equals(cookie.getProjectKey()))
                        .collect(Collectors.toList()));

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
        for (ParameterItem paramItem : paramItems) {
            if (isEmptyParameter(paramItem)) {
                log.debug("빈 파라미터 데이터 스킵 - name: {}, nameKo: {}, dataType: {}",
                        paramItem.getName(), paramItem.getNameKo(), paramItem.getDataType());
                continue;
            }

            ParameterEntity.ParameterEntityBuilder builder = ParameterEntity.builder()
                    .nameKo(paramItem.getNameKo())
                    .name(paramItem.getName())
                    .dataType(paramItem.getDataType())
                    .format(paramItem.getFormat())
                    .defaultValue(paramItem.getDefaultValue())
                    .required(paramItem.getRequired())
                    .description(paramItem.getDesc())
                    .apiListKey(apiList)
                    .categoryKey(findCategory(paramType))
                    .contextKey(findContext(paramType));

            if (paramItem.getLength() != null && paramItem.getLength() > 0) {
                builder.length(paramItem.getLength());
            }

            if (paramItem.getUpper() != null) {
                ParameterEntity parent = findParentParameter(paramItem.getUpper());
                if (parent == null) {
                    log.error("부모 파라미터를 찾을 수 없습니다. upper: {}", paramItem.getUpper());
                    throw new BusinessExceptionHandler(ErrorCode.NOT_FOUND_ERROR);
                }
                builder.parentKey(parent);
            }

            parameterRepository.save(builder.build());
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
        return contextRepository.findByName(name)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NOT_FOUND_ERROR));
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