// package com.sk.skala.axcalibur.sample.global.config;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Primary;
//
// import com.querydsl.jpa.JPQLTemplates;
// import com.querydsl.jpa.impl.JPAQueryFactory;
//
// import jakarta.persistence.EntityManager;
// import jakarta.persistence.PersistenceContext;
//
// /**
//  * QueryDSL 설정
//  * QueryDSL을 사용하지 않을 경우 의미 없음
//  *
//  * @author dig04214
//  */
//
// @Configuration
// public class QuerydslConfig {
//
// 	@PersistenceContext
// 	private EntityManager entityManager;
//
// 	@Bean
// 	@Primary
// 	public JPAQueryFactory jpaQueryFactory() {
// 		return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
// 	}
//
// 	@Bean
// 	public JPAQueryFactory queryFactory() {
// 		return new JPAQueryFactory(entityManager);
// 	}
//
// }
