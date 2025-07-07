package com.sk.skala.axcalibur.apitest.global.config;

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
	private static final String COOKIE_NAME = "avalon";

	Info info  = new Info()
		.title("AVALON ApiTest API")
		.description("AVALON ApiTest API Documentation")
		.version("0.0.1");


	SecurityScheme CookieScheme = new SecurityScheme()
		.name(COOKIE_NAME)
		.type(SecurityScheme.Type.APIKEY)
		.in(SecurityScheme.In.COOKIE)
		.description("'avalon' 쿠키 값을 입력하세요.");

	Components components = new Components()
		.addSecuritySchemes("avalon", CookieScheme);

	SecurityRequirement securityRequirement = new SecurityRequirement().addList("avalon");

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(info)
			.components(components)
			.addSecurityItem(securityRequirement);
	}
}
