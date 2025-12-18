package com.example.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing
@EnableWebSecurity
@EnableCaching
@EnableKafka
@EnableAsync
public class PortfolioApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortfolioApplication.class, args);
	}

}
