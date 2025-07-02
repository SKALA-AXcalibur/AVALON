package com.sk.skala.axcalibur.spec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpecApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpecApplication.class, args);
	}

}