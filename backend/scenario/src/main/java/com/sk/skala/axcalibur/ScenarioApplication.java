package com.sk.skala.axcalibur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.sk.skala.axcalibur")

public class ScenarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScenarioApplication.class, args);
	}

}
