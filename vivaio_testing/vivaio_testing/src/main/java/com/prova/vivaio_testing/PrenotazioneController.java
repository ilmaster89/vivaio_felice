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

	SimpleDateFormat sdfsql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm");
	ArrayList<Prenotazione> prenotazioniPossibili = new ArrayList();
	ArrayList<Prenotazione> autoInSedePrenotate = new ArrayList();

	public boolean collides(Prenotazione sql, Prenotazione web) {

		Date sqlInizio = null;
		Date sqlFine = null;
		Date webInizio = null;
		Date webFine = null;
		try {
			sqlInizio = sdfsql.parse(sql.getDataInizio());
			if (sql.getDataFine() == null)
				sqlFine = null;
			if (sql.getDataFine() != null)
				sqlFine = sdfsql.parse(sql.getDataFine());
			webInizio = sdf.parse(web.getDataInizio());
			webFine = sdf.parse(web.getDataFine());

			System.out.println("web inizio" + webInizio);
			System.out.println("web fine" + webFine);
			System.out.println(sqlInizio);
			System.out.println(sqlFine);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

		autoInSedePrenotate = new ArrayList();
		try {
			Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/vivaio_felice",
					"root", "password");
			Statement st = (Statement) conn.createStatement();
			String query = "select prenotazioni.*, auto.marca, auto.modello, auto.targa from auto left join prenotazioni on auto.id = prenotazioni.id_auto where auto.id in (select auto.id from parcheggio as p1 "
					+ "left outer join parcheggio as p2 on p1.id_auto=p2.id_auto and p1.data_parch < p2.data_parch "
					+ "join auto on p1.id_auto = auto.id " + "join sede on sede.id = p1.id_sede "
					+ "where p2.id is null and p1.id_sede = " + DipendenteController.logged.getIdSede() + ") "
					+ "group by prenotazioni.id";
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

			autoInSedePrenotate.remove(autoInSedePrenotate.size() - 1);
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
}
