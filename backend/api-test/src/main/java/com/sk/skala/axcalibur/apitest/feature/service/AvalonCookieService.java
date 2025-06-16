package com.sk.skala.axcalibur.apitest.feature.service;

import com.sk.skala.axcalibur.apitest.feature.entity.AvalonCookieEntity;

public interface AvalonCookieService {
  AvalonCookieEntity findByToken(String token);

}
