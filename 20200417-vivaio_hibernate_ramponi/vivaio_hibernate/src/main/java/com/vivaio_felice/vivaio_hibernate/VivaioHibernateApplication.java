package com.vivaio_felice.vivaio_hibernate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VivaioHibernateApplication {

	public static void main(String[] args) {
		// caricare variabili tramite dao in una classe con tutte le costanti
		SpringApplication.run(VivaioHibernateApplication.class, args);
	}

}
