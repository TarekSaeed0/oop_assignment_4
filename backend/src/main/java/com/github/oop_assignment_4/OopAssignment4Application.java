package com.github.oop_assignment_4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@SpringBootApplication
@EnableScheduling
public class OopAssignment4Application {
	public static void main(String[] args) {
		SpringApplication.run(OopAssignment4Application.class, args);
	}
}
