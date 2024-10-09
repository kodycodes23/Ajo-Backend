package com.ecobank.ecounion;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ecobank.ecounion.repository")
@EntityScan(basePackages = "com.ecobank.ecounion.model")

@SecurityScheme(name = "EcoAjo-api", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class EcoUnionApplication  {

	public static void main(String[] args) {
		SpringApplication.run(EcoUnionApplication.class, args);
	}

}
