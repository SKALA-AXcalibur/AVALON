package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.entity.AvalonCookieEntity;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface AvalonRepository extends CrudRepository<AvalonCookieEntity, String> {

  /**
   * 쿠키 안에 있는 토큰으로 아발론 쿠키 엔티티를 조회합니다.
   * @param token 쿠키 토큰
   * @return  아발론 쿠키 엔티티
   */
  Optional<AvalonCookieEntity> findAvalonCookieEntityByToken(String token);

}
