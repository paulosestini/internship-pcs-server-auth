package com.poli.internship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class InternshipApplication {
	public static void main(String[] args) {
		SpringApplication.run(InternshipApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello() {
		return "Hello!";
	}
}
