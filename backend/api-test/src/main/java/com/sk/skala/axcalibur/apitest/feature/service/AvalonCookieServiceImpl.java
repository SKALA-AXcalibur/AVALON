package com.sk.skala.axcalibur.apitest.feature.service;

import com.sk.skala.axcalibur.apitest.feature.entity.AvalonCookieEntity;
import com.sk.skala.axcalibur.apitest.feature.repository.AvalonRepository;
import com.sk.skala.axcalibur.apitest.global.code.ErrorCode;
import com.sk.skala.axcalibur.apitest.global.exception.BusinessExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvalonCookieServiceImpl implements AvalonCookieService {

  private final AvalonRepository repo;

  /**
   * 쿠키 안에 있는 토큰으로 쿠키 엔티티를 조회합니다.
   * @param token 쿠키 토큰
   * @return 쿠키 엔티티
   */
  @Override
  public AvalonCookieEntity findByToken(String token) {
    log.info("AvalonCookieServiceImpl.findByToken() called with token: {}", token);
    return repo.findByToken(token).orElseThrow(() -> {
      log.error("AvalonCookieEntity not found");
      return new BusinessExceptionHandler("", ErrorCode.UNAUTHORIZED_ERROR);
    });
  }
}
