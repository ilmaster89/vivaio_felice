package com.prova.vivaio_testing;

import java.util.Date;
import java.sql.DriverManager;
//FONDAMENTALE creare la path per il com.mysql.jdbc
import java.sql.ResultSet;
import java.sql.SQLException;
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

	// dichiaro due variabili per poterle usare in tutta la app, sono le due
	// variabili essenziali per contestualizzare le pagine.
	public static int lv = 0;
	public static int sede = 0;
	public static int patenteDip = 0;
	boolean neoP = false;
	Calendar cal = Calendar.getInstance();
	Date now = cal.getTime();

	// punto di partenza dell'applicazione.
	@GetMapping("/")
	public String index(Dipendente dipendente) {
		return "index";
	}

	// post dopo l'inserimento username e password.
	@PostMapping("/logged")
	public String loggedIn(Dipendente d) {

		// apro la connessione verso il DB.
		Connection conn = null;
		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/vivaio_felice", "root",
					"password");
			Statement st = (Statement) conn.createStatement();
			String sededip = "select id_sede, id_livello from dipendenti join sede_dip on dipendenti.id = sede_dip.id_dipendente where dipendenti.id = (select id from dipendenti where user_name ='"
					+ d.getUser_name() + "' and password = '" + d.getPassword() + "')";
			String patente = "select id_patente, data_possesso from dipendenti join possesso_patenti on dipendenti.id = possesso_patenti.id_dipendente where dipendenti.id = (select id from dipendenti where user_name ='"
					+ d.getUser_name() + "' and password = '" + d.getPassword() + "')";
			ResultSet rs = st.executeQuery(sededip);
			rs.next();
			sede = rs.getInt(1);
			lv = rs.getInt(2);
			rs = st.executeQuery(patente);
			rs.next();
			patenteDip = rs.getInt(1);
			Date dataPoss = rs.getDate(2);
			cal.add(Calendar.YEAR, -1);
			Date annoFa = cal.getTime();

			if (dataPoss.after(annoFa))
				neoP = true;

			System.out.println(neoP);
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// semplicissimo controllo sul dato ricevuto, non abbiamo creato alcun oggetto
		// dipendente e non abbiamo occupato memoria. A seconda del livello vengono
		// restituite le pagine corrispondenti.
		if (lv == 1) {
			lv = 0;
			return "OperaiPrimaPagina";
		}
		if (lv == 2) {
			return "Dipendenti_PrimaPagina";
		}

		if (lv == 3 || lv == 4) {
			return "Responsabile_PrimaPagina";
		}
		return "index";

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
					+ sede;
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				Integer id = rs.getInt(1);
				Integer id_livello = rs.getInt(2);
				String nome = rs.getString(3);
				String cognome = rs.getString(4);
				String userName = rs.getString(5);
				String pass = rs.getString(6);

				Dipendente dip = new Dipendente(id, id_livello, nome, cognome, userName, pass);
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

		mavDipendenti.setViewName("lista");
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

	@GetMapping("/OPprenota")
	public String OPprenota(Dipendente d) {
		return "Operai_PrenotaAuto";
	}

	@GetMapping("/OPkm")
	public String OPkm(Dipendente d) {
		return "Operai_InserimentoKm";
	}

	@GetMapping("/operai")
	public String operai(Dipendente d) {
		return "OperaiPrimaPagina";
	}

	@GetMapping("/impiegati")
	public String impiegati(Dipendente d) {
		return "Dipendenti_PrimaPagina";
	}

	@GetMapping("/responsabili")
	public String resp(Dipendente d) {
		return "Responsabile_PrimaPagina";
	}

	@GetMapping("/IMPinsdip")
	public String insdip(Dipendente d) {
		return "Dipendenti_InserimentoDip-";
	}

	@GetMapping("/IMPmanu")
	public String manu(Dipendente d) {
		return "Dipendenti_Manutenzione";
	}

	@GetMapping("/IMPspese")
	public String spese(Dipendente d) {
		return "Dipendenti_Spese";
	}

	@GetMapping("/IMPdash")
	public String dash(Dipendente d) {
		return "Dipendenti_DashBoard";
	}

	@GetMapping("/RESprenota")
	public String RP(Dipendente d) {
		return "Responsabile_Prenota";
	}

	@GetMapping("/RESkm")
	public String RK(Dipendente d) {
		return "Responsabile_InserimentoKm";
	}

	@GetMapping("/RESmanu")
	public String RM(Dipendente d) {
		return "Responsabile_Manutenzione";
	}

	@GetMapping("/RESspese")
	public String RS(Dipendente d) {
		return "Responsabile_Spese";
	}

	@GetMapping("/REStrans")
	public String RT(Dipendente d) {
		return "Responsabile_Trasferimenti";
	}

	@GetMapping("/RESdash")
	public String RD(Dipendente d) {
		return "Responsabile_Dashboard";
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
			int idNuovoDipendente = 0;

			st.executeUpdate(update1);
			ResultSet rs = st.executeQuery(idDip);
			rs.next();
			idNuovoDipendente = rs.getInt(1);
			String update2 = "INSERT INTO `vivaio_felice`.`sede_dip` (`id_dipendente`, `id_sede`) VALUES ('"
					+ idNuovoDipendente + "', '" + sede + "');";
			String update3 = "INSERT INTO `vivaio_felice`.`possesso_patenti` (`id_dipendente`, `id_patente`, `data_possesso`) VALUES ('"
					+ idNuovoDipendente + "', '" + d.getIdPatente() + "', '" + d.getDataPossesso() + "');";
			st.executeUpdate(update2);
			st.executeUpdate(update3);
			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "redirect:/IMPinsdip";
	}
}
