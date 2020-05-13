package com.vivaio_felice.vivaio_hibernate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class VivaioHibernateApplication {

	public static void main(String[] args) {
		// caricare variabili tramite dao in una classe con tutte le costanti
		SpringApplication.run(VivaioHibernateApplication.class, args);
	}

}
