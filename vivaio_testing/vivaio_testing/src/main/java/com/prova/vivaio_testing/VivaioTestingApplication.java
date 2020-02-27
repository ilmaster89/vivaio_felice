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

		Connection conn = null;
		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/vivaio_felice", "root",
					"InfySQL899");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Statement st = null;

		try {
			st = (Statement) conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String query = "select * from dipendenti";
		ResultSet rs = null;
		try {
			rs = st.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// questa è la differenza fondamentale.
		try {
			while (rs.next()) {

				// a differenza del codice precedente, qui non vado a stampare tutti contenuti
				// delle colonne, ma li prendo e li assegno ad un costruttore. Ovviamente
				// bisogna scrivere a mano le singole variabili ma questo non è un problema in
				// quanto noi conosciamo il DB.
				Integer id = (Integer) rs.getObject(1);
				Integer id_livello = (Integer) rs.getObject(2);
				String nome = (String) rs.getObject(3);
				String cognome = (String) rs.getObject(4);
				String user_name = (String) rs.getObject(5);
				String password = (String) rs.getObject(6);

				// ad ogni ciclo, in questo modo, vengono presi degli attributi (riferiti a
				// diverse serie di dati) e vengono creati nuovi dipendenti, aggiunti subito
				// alla ArrayList. Ogni ciclo li resetta, quindi non si pone il problema di
				// inserire lo stesso dipendente più volte, anche senza cambiare nome.
				Dipendente d = new Dipendente(id, id_livello, nome, cognome, user_name, password);
				dipendenti.add(d);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ArrayList<Dipendente> getDipendenti() {
		return dipendenti;
	}
}
