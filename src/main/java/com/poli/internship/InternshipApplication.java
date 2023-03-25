package com.poli.internship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class InternshipApplication {
	public static final Logger LOGGER = LoggerFactory.getLogger(InternshipApplication.class);
	public static void main(String[] args) {

		SpringApplication.run(InternshipApplication.class, args);
	}
}
