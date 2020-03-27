package com.prova.vivaio_testing;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

@Controller
public class PrenotazioneController {

	Prenotazione ultimaPrenotazione = null;
	SimpleDateFormat sdfsql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm");
	SimpleDateFormat toSQL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	ArrayList<Prenotazione> prenotazioniPossibili = new ArrayList();
	ArrayList<Prenotazione> autoInSedePrenotate = new ArrayList();

	public boolean collides(Prenotazione sql, Prenotazione web) {

		Date sqlInizio = null;
		Date sqlFine = null;
		Date webInizio = null;
		Date webFine = null;
		try {
			if (sql.getDataInizio() == null)
				sqlInizio = null;
			if (sql.getDataInizio() != null)
				sqlInizio = sdfsql.parse(sql.getDataInizio());
			if (sql.getDataFine() == null)
				sqlFine = null;
			if (sql.getDataFine() != null)
				sqlFine = sdfsql.parse(sql.getDataFine());
			webInizio = sdf.parse(web.getDataInizio());
			webFine = sdf.parse(web.getDataFine());

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (sqlInizio == null)
			return true;

		if (sqlInizio.after(webInizio)) {

			if (webInizio.before(sqlInizio) && webFine.before(sqlFine))
				return true;
			if (webInizio.before(sqlInizio) && webFine.after(sqlFine))
				return true;
			if (webInizio.after(sqlInizio) && webFine.before(sqlFine))
				return true;
			if (webInizio.after(sqlInizio) && webFine.after(sqlFine))
				return true;
			if (webInizio.before(sqlInizio) && webFine.after(sqlInizio) && sqlFine == null)
				return true;
			if (webInizio.after(sqlInizio) && sqlFine == null)
				return true;
		}

		return false;

	}

	@GetMapping("/prenota")
	public ModelAndView prenota(Prenotazione p) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("prenota");
		mav.addObject("logged", DipendenteController.logged);
		return mav;
	}

	@PostMapping("/richiestadiprenotazione")
	public ModelAndView listaPrenotabili(Prenotazione web) {

		prenotazioniPossibili = new ArrayList();
		autoInSedePrenotate = new ArrayList();

		Date dataRichiesta1 = null;
		try {
			dataRichiesta1 = sdf.parse(web.getDataInizio());
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String dataRichiesta2 = toSQL.format(dataRichiesta1);
		System.out.println(dataRichiesta2);

		try {
			Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/vivaio_felice",
					"root", "password");
			Statement st = (Statement) conn.createStatement();
			String query = "select prenotazioni.*, auto.marca, auto.modello, auto.targa from auto left outer join prenotazioni on auto.id = prenotazioni.id_auto where auto.id in (select auto.id from parcheggio as p1 "
					+ "left outer join parcheggio as p2 on p1.id_auto=p2.id_auto and p1.data_parch < p2.data_parch "
					+ "join auto on p1.id_auto = auto.id " + "join sede on sede.id = p1.id_sede "
					+ "where p2.id is null and p1.id_sede = " + DipendenteController.logged.getIdSede() + ") "
					+ "and `data_inizio` is null or `data_inizio` > '" + dataRichiesta2 + "' group by auto.id";

			ResultSet rs = st.executeQuery(query);

			while (rs.next()) {
				Integer id = rs.getInt(1);
				Integer idDip = rs.getInt(2);
				Integer idAuto = rs.getInt(3);
				Integer idCausale = rs.getInt(4);
				String dataInizio = rs.getString(5);
				String dataFine = rs.getString(6);
				Integer km = rs.getInt(7);
				String marca = rs.getString(8);
				String modello = rs.getString(9);
				String targa = rs.getString(10);

				Prenotazione sql = new Prenotazione(id, idDip, idAuto, idCausale, dataInizio, dataFine, km, marca,
						modello, targa);
				autoInSedePrenotate.add(sql);
			}

//			autoInSedePrenotate.remove(autoInSedePrenotate.size() - 1);
			conn.close();

			for (Prenotazione sql : autoInSedePrenotate) {
				System.out.println(sql.toString());
				if (collides(sql, web))
					prenotazioniPossibili.add(sql);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ModelAndView mav = new ModelAndView();
		mav.setViewName("prenopossibili");
		mav.addObject("prenotazioni", prenotazioniPossibili);
		return mav;
	}

	@GetMapping("/km")
	public ModelAndView km(Prenotazione p) {

		ultimaPrenotazione = null;

		ModelAndView mav = new ModelAndView();
		try {
			Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/vivaio_felice",
					"root", "password");
			Statement st = (Statement) conn.createStatement();
			String query = "select prenotazioni.*, marca, modello ,targa "
					+ "from dipendenti join prenotazioni on dipendenti.id = prenotazioni.id_dipendente "
					+ "join auto on prenotazioni.id_auto = auto.id where km is null and id_dipendente = "
					+ DipendenteController.logged.getId() + " order by data_fine desc limit 1";
			ResultSet rs = st.executeQuery(query);

			if (rs.next()) {
				Integer id = rs.getInt(1);
				Integer idDip = rs.getInt(2);
				Integer idAuto = rs.getInt(3);
				Integer idCausale = rs.getInt(4);
				String dataInizio = rs.getString(5);
				String dataFine = rs.getString(6);
				Integer km = rs.getInt(7);
				String marca = rs.getString(8);
				String modello = rs.getString(9);
				String targa = rs.getString(10);
				ultimaPrenotazione = new Prenotazione(id, idDip, idAuto, idCausale, dataInizio, dataFine, km, marca,
						modello, targa);
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mav.setViewName("inserimentoKm");
		mav.addObject("logged", DipendenteController.logged);
		mav.addObject("preno", ultimaPrenotazione);
		return mav;
	}

	@PostMapping("/kminseriti")
	public ModelAndView kminst(Prenotazione p) {
		ModelAndView mav = new ModelAndView();

		try {
			Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/vivaio_felice",
					"root", "password");
			Statement st = (Statement) conn.createStatement();
			String update = "UPDATE `vivaio_felice`.`prenotazioni` SET `km` = '" + p.getKm() + "' WHERE (`id` = '"
					+ ultimaPrenotazione.getId() + "')";
			st.executeUpdate(update);
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mav.setViewName("inserimentoKm");
		mav.addObject("logged", DipendenteController.logged);
		return mav;
	}
}
