package com.sk.skala.axcalibur.spec.repository;

import com.sk.skala.axcalibur.spec.entity.SpecFileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpecFileRepository extends JpaRepository<SpecFileEntity, Integer> {

    // 프로젝트별 파일 목록 조회 (전체)
    List<SpecFileEntity> findByProjectKey(Integer projectKey);

    // 프로젝트별 파일 목록 조회 (페이징, 생성일자 기준 내림차순)
    Page<SpecFileEntity> findByProjectKeyOrderByCreatedAtDesc(Integer projectKey, Pageable pageable);

    // 특정 경로의 파일 존재 여부 확인
    boolean existsByPath(String path);

    // 특정 프로젝트의 특정 경로 파일 조회
    Optional<SpecFileEntity> findByProjectKeyAndPath(Integer projectKey, String path);

    // 특정 프로젝트의 파일 개수 조회
    long countByProjectKey(Integer projectKey);

    // 특정 날짜 이후에 생성된 파일들 조회
    @Query("SELECT s FROM SpecFileEntity s WHERE s.createdAt >= :fromDate")
    List<SpecFileEntity> findFilesCreatedAfter(@Param("fromDate") LocalDateTime fromDate);

    // 특정 경로 패턴으로 파일 검색
    List<SpecFileEntity> findByPathContaining(String pathPattern);

    // 프로젝트와 경로 패턴으로 파일 검색 (페이징 지원)
    Page<SpecFileEntity> findByProjectKeyAndPathContainingOrderByCreatedAtDesc(Integer projectKey, String pathPattern, Pageable pageable);
}