package com.sk.skala.axcalibur.spec.global.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;    

@Component
public class DataInitializerConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializeData() {
        // 우선순위 데이터
        jdbcTemplate.execute("INSERT IGNORE INTO priority (`key`, name) VALUES (1, 'HIGH')");
        jdbcTemplate.execute("INSERT IGNORE INTO priority (`key`, name) VALUES (2, 'MEDIUM')");
        jdbcTemplate.execute("INSERT IGNORE INTO priority (`key`, name) VALUES (3, 'LOW')");

        // 파일 타입 데이터
        jdbcTemplate.execute("INSERT IGNORE INTO file_type (`key`, name) VALUES (1, 'REQUIREMENT')");
        jdbcTemplate.execute("INSERT IGNORE INTO file_type (`key`, name) VALUES (2, 'INTERFACE_DEFINITION')");
        jdbcTemplate.execute("INSERT IGNORE INTO file_type (`key`, name) VALUES (3, 'INTERFACE_DESIGN')");
        jdbcTemplate.execute("INSERT IGNORE INTO file_type (`key`, name) VALUES (4, 'ERD')");

        // 카테고리 데이터
        jdbcTemplate.execute("INSERT IGNORE INTO category (`key`, name) VALUES (1, 'PATH/QUERY')");
        jdbcTemplate.execute("INSERT IGNORE INTO category (`key`, name) VALUES (2, 'REQUEST')");
        jdbcTemplate.execute("INSERT IGNORE INTO category (`key`, name) VALUES (3, 'RESPONSE')");

        // 컨텍스트 데이터
        jdbcTemplate.execute("INSERT IGNORE INTO context (`key`, name) VALUES (1, 'BODY')");
        jdbcTemplate.execute("INSERT IGNORE INTO context (`key`, name) VALUES (2, 'HEADER')");
        jdbcTemplate.execute("INSERT IGNORE INTO context (`key`, name) VALUES (3, 'QUERY')");
        jdbcTemplate.execute("INSERT IGNORE INTO context (`key`, name) VALUES (4, 'PATH')");
    }
} 