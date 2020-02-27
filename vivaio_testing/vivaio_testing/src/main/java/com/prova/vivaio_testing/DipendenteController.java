package com.prova.vivaio_testing;

import java.sql.DriverManager;

//FONDAMENTALE creare la path per il com.mysql.jdbc
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

@Controller
public class DipendenteController {

	// dichiaro la ArrayList che servirà da contenitore per tutti i dipendenti.
	ArrayList<Dipendente> dipendenti = new ArrayList();

	@GetMapping("/")
	public String index(Dipendente dipendente) {

		// questo risulterà familiare. Creo una connessione per prendere i dati dalla
		// cartella dei dipendenti-
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
		return "home";
	}

	@PostMapping("/logged")
	public String loggedIn(Dipendente d) {

		// al controllo per il login, come avevo accennato, ho aggiunto il controllo per
		// l'id livello, che naturalmente porta a diverse pagine, come potrete
		// verificare da voi.
		// Una cosa curiosa su cui dovremo lavorare: se il controllo sull'id livello lo
		// faccio su "d", ovvero l'argomento passato al metodo, mi crea problemi. Se
		// invece lo faccio sul dipendente nell'ArrayList funziona perfettamente. Ci
		// lavoreremo, ma l'importante è che funzioni.
		for (Dipendente dip : dipendenti) {
			if (d.getUser_name().equals(dip.getUser_name()) && d.getPassword().equals(dip.getPassword())
					&& dip.getId_livello() == 1)
				return "operai";
			if (d.getUser_name().equals(dip.getUser_name()) && d.getPassword().equals(dip.getPassword())
					&& dip.getId_livello() == 2)
				return "impiegati";
			if (d.getUser_name().equals(dip.getUser_name()) && d.getPassword().equals(dip.getPassword())
					&& dip.getId_livello() == 3)
				return "responsabili";
			if (d.getUser_name().equals(dip.getUser_name()) && d.getPassword().equals(dip.getPassword())
					&& dip.getId_livello() == 4)
				return "titolare";

		}

		return "home";

	}

	@GetMapping("/lista")
	public ModelAndView lista() {

		ModelAndView mav = new ModelAndView();

		mav.setViewName("lista");
		mav.addObject("dipendenti", dipendenti);

		return mav;
	}

}
