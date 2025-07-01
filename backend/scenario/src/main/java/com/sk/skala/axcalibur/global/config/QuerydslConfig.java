package com.sk.skala.axcalibur.global.config;

import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Querydsl 설정 클래스입니다.
 * <p>
 * JPAQueryFactory를 스프링 빈으로 등록하여 Querydsl을 사용할 수 있도록 합니다.
 * EntityManager는 @PersistenceContext로 주입받아 트랜잭션 및 영속성 컨텍스트를 관리합니다.
 * <ul>
 *   <li>jpaQueryFactory(): JPQLTemplates를 명시적으로 지정한 JPAQueryFactory 빈을 생성합니다. (@Primary)</li>
 *   <li>queryFactory(): EntityManager만 사용한 JPAQueryFactory 빈을 생성합니다.</li>
 * </ul>
 */
@Configuration // Querydsl 관련 설정을 위한 스프링 설정 클래스
public class QuerydslConfig {

  /**
   * JPA의 EntityManager를 주입받아 트랜잭션 및 영속성 컨텍스트를 관리합니다.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * JPQLTemplates를 명시적으로 지정하여 JPAQueryFactory 빈을 생성합니다.
   * 동일 타입 빈이 여러 개일 때 우선적으로 주입됩니다.
   * @return JPAQueryFactory
   */
  @Bean
  @Primary
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
  }

//  /**
//   * EntityManager만 사용하여 JPAQueryFactory 빈을 생성합니다.
//   * @return JPAQueryFactory
//   */
//  @Bean
//  public JPAQueryFactory queryFactory() {
//    return new JPAQueryFactory(entityManager);
//  }
}