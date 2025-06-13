package com.sk.skala.axcalibur.apitest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ApiTestApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiTestApplication.class, args);
  }

}
