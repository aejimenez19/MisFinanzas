package com.aejimenezdev.misfinanzas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MisFinanzasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MisFinanzasApplication.class, args);
	}

}
