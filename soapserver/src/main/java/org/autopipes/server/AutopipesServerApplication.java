package org.autopipes.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan( basePackages = {"org.autopipes.model"} )
public class AutopipesServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutopipesServerApplication.class, args);
	}
}
