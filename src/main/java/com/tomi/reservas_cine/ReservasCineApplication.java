package com.tomi.reservas_cine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReservasCineApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservasCineApplication.class, args);
	}
}