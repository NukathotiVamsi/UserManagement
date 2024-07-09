package com.example.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication

@EnableWebMvc
public class UsermanagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(UsermanagementApplication.class, args);
		System.out.println("changes in main application");
	}
}
