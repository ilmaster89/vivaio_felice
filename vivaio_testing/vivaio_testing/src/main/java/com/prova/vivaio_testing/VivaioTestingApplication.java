package com.prova.vivaio_testing;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

@SpringBootApplication
public class VivaioTestingApplication {

	public static ArrayList<Dipendente> dipendenti = new ArrayList();

	public static void main(String[] args) {
		SpringApplication.run(VivaioTestingApplication.class, args);
	}

	public static ArrayList<Dipendente> getDipendenti() {
		return dipendenti;
	}
}
