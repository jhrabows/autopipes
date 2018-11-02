package org.autopipes.springboot.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootApplication implements CommandLineRunner{
	private static Logger LOG = LoggerFactory.getLogger(SpringbootApplication.class);
	
	public static void main(String[] args) {
		LOG.info("+main");
		//
		SpringApplication.run(SpringbootApplication.class, args);
				
		LOG.info("-main");
	}
	
   @Override
    public void run(String... args) throws Exception {
	   LOG.info("+run");
	   LOG.info("-run");
    }
}
