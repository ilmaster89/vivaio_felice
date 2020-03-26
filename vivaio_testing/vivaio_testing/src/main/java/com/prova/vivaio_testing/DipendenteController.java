package com.prova.vivaio_testing;

import java.sql.Date;
import java.sql.DriverManager;
//FONDAMENTALE creare la path per il com.mysql.jdbc
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

@Controller
public class DipendenteController {

	// preparo la lista dei dipendenti "ridotti" per richiamarla come modelandview.
	ArrayList<Dipendente> dipendentiInSede = new ArrayList();

	public static Dipendente logged = null;

	// punto di partenza dell'applicazione.
	@GetMapping("/")
	public String index(Dipendente dipendente) {
		return "index";

	}

	// post dopo l'inserimento username e password.
	// momentaneo, da cambiare con Antonio.
	@PostMapping("/logged")
	public ModelAndView loggedIn(Dipendente d) {

		ModelAndView utenteLoggato = new ModelAndView();
		// apro la connessione verso il DB.
		Connection conn = null;
		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/vivaio_felice", "root",
					"password");
			Statement st = (Statement) conn.createStatement();
			String sededip = "select nome, cognome, id_dipendente, id_sede, id_livello from dipendenti join sede_dip on dipendenti.id = sede_dip.id_dipendente where dipendenti.id = (select id from dipendenti where user_name ='"
					+ d.getUser_name() + "' and password = '" + d.getPassword() + "')";
			String patente = "select id_patente, data_possesso from dipendenti join possesso_patenti on dipendenti.id = possesso_patenti.id_dipendente where dipendenti.id = (select id from dipendenti where user_name ='"
					+ d.getUser_name() + "' and password = '" + d.getPassword() + "')";

			ResultSet rs = st.executeQuery(sededip);
			rs.next();
			String nome = rs.getString(1);
			String cognome = rs.getString(2);
			Integer idDip = rs.getInt(3);
			Integer idSede = rs.getInt(4);
			Integer idLivello = rs.getInt(5);
			rs = st.executeQuery(patente);
			rs.next();
			Integer idPatente = rs.getInt(1);
			Date dataPossesso = rs.getDate(2);

			logged = new Dipendente(idDip, idLivello, nome, cognome, d.getUser_name(), d.getPassword(), idPatente,
					idSede, dataPossesso);

			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// semplicissimo controllo sul dato ricevuto, non abbiamo creato alcun oggetto
		// dipendente e non abbiamo occupato memoria. A seconda del livello vengono
		// restituite le pagine corrispondenti.

		utenteLoggato.setViewName("primapaginatotale");
		utenteLoggato.addObject("logged", logged);

		return utenteLoggato;

	}

	// rintraccia tutti i dipendenti presenti nella sede dell'impiegato che li
	// richiede.
	@GetMapping("/listaDIP")
	public ModelAndView lista(Dipendente d) {

		dipendentiInSede = new ArrayList();
		Connection conn = null;
		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/vivaio_felice", "root",
					"password");
			Statement st = (Statement) conn.createStatement();
			String query = "select dipendenti.id, id_livello, nome, cognome, user_name, password from dipendenti join sede_dip on dipendenti.id = sede_dip.id_dipendente where sede_dip.id_sede ="
					+ logged.getIdSede();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				Integer id = rs.getInt(1);
				Integer id_livello = rs.getInt(2);
				String nome = rs.getString(3);
				String cognome = rs.getString(4);
				String userName = rs.getString(5);
				String pass = rs.getString(6);

				Dipendente dip = new Dipendente(id, id_livello, nome, cognome, userName, pass, null, null, null);
				dipendentiInSede.add(dip);

			}
			conn.close();

		} catch (

		SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// semplicemente mostro la lista ottenuta.
		ModelAndView mavDipendenti = new ModelAndView();

		mavDipendenti.setViewName("listaDipendenti");
		mavDipendenti.addObject("dipendenti", dipendentiInSede);

		return mavDipendenti;
	}

	@GetMapping("/dettaglioutente")
	public ModelAndView dettaglioUtente(@RequestParam(name = "id") Integer id) {

		ModelAndView dettUtente = new ModelAndView();
		for (Dipendente d : dipendentiInSede) {
			if (d.getId() == id) {
				dettUtente.setViewName("dettaglioutente");
				dettUtente.addObject("utente", d);
				return dettUtente;
			}

		}

		return null;
	}

	

	@GetMapping("/indietro")
	public ModelAndView back(Dipendente d) {
		ModelAndView back = new ModelAndView();
		back.setViewName("primapaginatotale");
		back.addObject("logged", logged);
		return back;
	}

	@GetMapping("/insdip")
	public ModelAndView inse(Dipendente d) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("inserimentoDipendenti");
		mav.addObject("logged", logged);
		return mav;
	}

	@GetMapping("/manu")
	public ModelAndView manu(Dipendente d) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("manutenzione");
		mav.addObject("logged", logged);
		return mav;
	}

	@GetMapping("/spese")
	public ModelAndView spese(Dipendente d) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("spese");
		mav.addObject("logged", logged);
		return mav;
	}

	@GetMapping("/dash")
	public ModelAndView dash(Dipendente d) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("dashboard");
		mav.addObject("logged", logged);
		return mav;
	}

	@GetMapping("/trans")
	public ModelAndView trans(Dipendente d) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("trasferimenti");
		mav.addObject("logged", logged);
		return mav;
	}

	@PostMapping("/insert")
	public String insdipendente(Dipendente d) {

		try {
			Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/vivaio_felice",
					"root", "password");
			Statement st = (Statement) conn.createStatement();
			String update1 = "INSERT INTO `vivaio_felice`.`dipendenti` (`id_livello`, `nome`, `cognome`, `user_name`, `password`) VALUES ('"
					+ d.getId_livello() + "', '" + d.getNome() + "', '" + d.getCognome() + "', '" + d.getUser_name()
					+ "', '" + d.getPassword() + "')";
			String idDip = "select id from dipendenti order by id desc limit 1";

			st.executeUpdate(update1);
			ResultSet rs = st.executeQuery(idDip);
			rs.next();
			Integer idNuovoDipendente = rs.getInt(1);
			String update2 = "INSERT INTO `vivaio_felice`.`sede_dip` (`id_dipendente`, `id_sede`) VALUES ('"
					+ idNuovoDipendente + "', '" + logged.getIdSede() + "');";
			String update3 = "INSERT INTO `vivaio_felice`.`possesso_patenti` (`id_dipendente`, `id_patente`, `data_possesso`) VALUES ('"
					+ idNuovoDipendente + "', '" + d.getIdPatente() + "', '" + d.getDataPossesso() + "');";
			st.executeUpdate(update2);
			st.executeUpdate(update3);
			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "redirect:/insdip";
	}
}
