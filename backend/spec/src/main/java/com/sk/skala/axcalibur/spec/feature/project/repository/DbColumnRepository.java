package com.sk.skala.axcalibur.spec.feature.project.repository;

import com.sk.skala.axcalibur.spec.feature.project.entity.DbColumnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbColumnRepository extends JpaRepository<DbColumnEntity, Integer> {
    // 필요하다면 커스텀 쿼리 메서드 추가
}
