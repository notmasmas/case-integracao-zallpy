package com.branch_master.ecovolt360; // Your actual package name

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Ecovolt360Application {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.directory("./backend")
				.ignoreIfMissing()
				.load();

		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

		SpringApplication.run(Ecovolt360Application.class, args);
	}
}