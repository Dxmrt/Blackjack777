package com.blackjack.blackjack777;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.blackjack.blackjack777")
public class Blackjack777Application {

	public static void main(String[] args) {
		SpringApplication.run(Blackjack777Application.class, args);
	}

}
