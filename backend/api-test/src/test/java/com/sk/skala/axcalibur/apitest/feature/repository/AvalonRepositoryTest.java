package com.sk.skala.axcalibur.apitest.feature.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.sk.skala.axcalibur.apitest.feature.entity.AvalonCookieEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AvalonRepositoryTest {

  @Autowired
  AvalonRepository repo;


  @Test
  void findByToken() {
  }

  @Test
  void saveEntity() {
    // Given
    AvalonCookieEntity entity = AvalonCookieEntity.builder()
        .token("asdfasdf")
        .projectKey(1)
        .build();

    repo.save(entity);
  }

}