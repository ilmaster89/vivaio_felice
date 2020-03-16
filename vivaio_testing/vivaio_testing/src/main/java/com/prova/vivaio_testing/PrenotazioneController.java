package com.prova.vivaio_testing;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

@Controller
public class PrenotazioneController {

	DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;

	ArrayList<Prenotazione> prenotazioniPossibili = new ArrayList();
	ArrayList<Prenotazione> autoInSedePrenotate = new ArrayList();

	public boolean collides(Prenotazione sql, Prenotazione web) {
		if (web.getDataInizio().before(sql.getDataInizio()) && web.getDataFine().before(sql.getDataFine()))
			return true;
		if (web.getDataInizio().before(sql.getDataInizio()) && web.getDataFine().after(sql.getDataFine()))
			return true;
		if (web.getDataInizio().after(sql.getDataInizio()) && web.getDataFine().before(sql.getDataFine()))
			return true;
		if (web.getDataInizio().after(sql.getDataInizio()) && web.getDataFine().after(sql.getDataFine()))
			return true;
		if (web.getDataInizio().before(sql.getDataInizio()) && web.getDataFine().after(sql.getDataInizio())
				&& sql.getDataFine() == null)
			return true;
		if (web.getDataInizio().after(sql.getDataInizio()) && sql.getDataFine() == null)
			return true;

		return false;

	}

	@GetMapping("/OPprenota")
	public String OPprenota(Prenotazione p) {
		return "Operai_PrenotaAuto";
	}

	@PostMapping("/richiestadiprenotazione")
	public ModelAndView listaPrenotabili(Prenotazione web) {

		autoInSedePrenotate = new ArrayList();
		try {
			Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/vivaio_felice",
					"root", "InfySQL899");
			Statement st = (Statement) conn.createStatement();
			String query = "select prenotazioni.*, auto.marca, auto.modello, auto.targa from auto left join prenotazioni on auto.id = prenotazioni.id_auto where auto.id in (select auto.id from parcheggio as p1 "
					+ "left outer join parcheggio as p2 on p1.id_auto=p2.id_auto and p1.data_parch < p2.data_parch "
					+ "join auto on p1.id_auto = auto.id " + "join sede on sede.id = p1.id_sede "
					+ "where p2.id is null and p1.id_sede = " + DipendenteController.sede + ") "
					+ "group by prenotazioni.id";
			ResultSet rs = st.executeQuery(query);

			while (rs.next()) {
				Integer id = rs.getInt(1);
				Integer idDip = rs.getInt(2);
				Integer idAuto = rs.getInt(3);
				Integer idCausale = rs.getInt(4);
				Timestamp dataInizio = rs.getTimestamp(5);
				Timestamp dataFine = rs.getTimestamp(6);
				Integer km = rs.getInt(7);
				String marca = rs.getString(8);
				String modello = rs.getString(9);
				String targa = rs.getString(10);

				Prenotazione sql = new Prenotazione(id, idDip, idAuto, idCausale, dataInizio, dataFine, km, marca,
						modello, targa);
				autoInSedePrenotate.add(sql);
			}

			conn.close();

			for (Prenotazione sql : autoInSedePrenotate) {
				if (!collides(sql, web))
					prenotazioniPossibili.add(web);

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
