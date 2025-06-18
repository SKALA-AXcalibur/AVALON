package com.sk.skala.axcalibur.scenario.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 설정
 *
 * @author dig04214
 */
@Configuration
public class SwaggerConfig {
	Info info  = new Info()
		.title("AVALON Sample API")
		.description("AVALON Sample API Documentation")
		.version("0.0.1");

	String sessionCookieName = "JSESSIONID";

	SecurityScheme sessionAuthScheme = new SecurityScheme()
		.name(sessionCookieName)
		.type(SecurityScheme.Type.APIKEY)
		.in(SecurityScheme.In.COOKIE)
		.description(String.format("애플리케이션 세션 쿠키 (%s). 사용자가 로그인하면 브라우저에 의해 자동으로 관리되며, API 요청 시 함께 전송됩니다.", sessionCookieName));

	Components components = new Components()
		.addSecuritySchemes("Session", sessionAuthScheme);

	SecurityRequirement securityRequirement = new SecurityRequirement().addList("Session");

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(info)
			.components(components)
			.addSecurityItem(securityRequirement);
	}
}
