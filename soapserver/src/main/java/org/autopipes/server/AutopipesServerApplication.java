package org.autopipes.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = { "org.autopipes.service", "org.autopipes.server.config" })
@EntityScan( basePackages = {"org.autopipes.model"} )
@ComponentScan(basePackages = {"org.autopipes.service", "org.autopipes.server.config"})
@EnableJpaRepositories("org.autopipes.service")
public class AutopipesServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutopipesServerApplication.class, args);
	}
}
