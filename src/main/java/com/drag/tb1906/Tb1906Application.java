package com.drag.tb1906;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@ComponentScan(basePackages = { "com.drag.tb1906"})
@EnableJpaRepositories
@SpringBootApplication
@EnableScheduling
public class Tb1906Application {

	public static void main(String[] args) {
		SpringApplication.run(Tb1906Application.class, args);
	}
}
