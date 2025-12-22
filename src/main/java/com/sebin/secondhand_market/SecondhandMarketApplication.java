package com.sebin.secondhand_market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SecondhandMarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecondhandMarketApplication.class, args);
	}

}
