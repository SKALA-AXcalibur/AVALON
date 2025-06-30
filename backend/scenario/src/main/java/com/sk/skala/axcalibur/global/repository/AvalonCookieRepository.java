package com.sk.skala.axcalibur.global.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.axcalibur.global.entity.AvalonCookieEntity;


@Repository
public interface AvalonCookieRepository extends CrudRepository<AvalonCookieEntity, String> {
    // 기본적으로 token 기반 findById 가능
}