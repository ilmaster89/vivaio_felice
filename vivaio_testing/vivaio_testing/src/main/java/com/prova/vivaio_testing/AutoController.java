package com.prova.vivaio_testing;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Date;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

@Controller
public class AutoController {

	// come per il dipendente, preparo la lista per mostrare le auto nella sede.
	ArrayList<Auto> autoInSede = new ArrayList();

	@GetMapping("/insauto")
	public ModelAndView insAuto(Auto a) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("inserimentoAuto");
		mav.addObject("logged", DipendenteController.logged);
		return mav;
	}

	// inserimento auto.
	@PostMapping("/insertAuto")
	public String autoInserita(Auto a) {

		try {
			Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/vivaio_felice",
					"root", "password");
			Statement st = (Statement) conn.createStatement();
			String update1 = "INSERT INTO `vivaio_felice`.`auto` (`id_patente`, `carburante`, `marca`, `modello`, `targa`, `kw`, `tara`, `data_assicurazione`) "
					+ "VALUES ('" + a.getIdPatente() + "', '" + a.getCarburante() + "', '" + a.getMarca() + "', '"
					+ a.getModello() + "', '" + a.getTarga() + "', '" + a.getKw() + "', '" + a.getTara() + "', '"
					+ a.getDataAssicurazione() + "')";
			String idAuto = "select id from auto order by id desc limit 1";
			int idNuovaAuto = 0;
			st.executeUpdate(update1);
			ResultSet rs = st.executeQuery(idAuto);
			rs.next();
			idNuovaAuto = rs.getInt(1);
			String update2 = "INSERT INTO `vivaio_felice`.`parcheggio` (`id_auto`, `id_sede`, `data_parch`) VALUES ('"
					+ idNuovaAuto + "', '" + DipendenteController.logged.getIdSede() + "', '" + LocalDate.now() + "');";
			st.executeUpdate(update2);
			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "redirect:/insauto";
	}

	// lista delle auto in sede. Funziona esattamente come la lista dei dipendenti,
	// con le ovvie differenze relative alla tabella e ai dati da recuperare.
	@GetMapping("/listaAUTO")
	public ModelAndView listaAuto(Dipendente d) {

		autoInSede = new ArrayList();
		try {
			Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/vivaio_felice",
					"root", "password");
			Statement st = (Statement) conn.createStatement();
			String query = "select auto.id, id_patente,carburante, marca, modello, targa, kw, tara, data_assicurazione from parcheggio as p1 "
					+ "left outer join parcheggio as p2 on p1.id_auto=p2.id_auto and p1.data_parch < p2.data_parch "
					+ "join auto on p1.id_auto = auto.id " + "join sede on sede.id = p1.id_sede "
					+ "where p2.id is null and p1.id_sede = " + DipendenteController.logged.getIdSede();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				Integer id = rs.getInt(1);
				Integer idPat = rs.getInt(2);
				String carburante = rs.getString(3);
				String marca = rs.getString(4);
				String modello = rs.getString(5);
				String targa = rs.getString(6);
				Integer kw = rs.getInt(7);
				Integer tara = rs.getInt(8);
				Date dataAss = rs.getDate(9);

				Auto a = new Auto(id, idPat, carburante, marca, modello, targa, kw, tara, dataAss);
				autoInSede.add(a);

			}
			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ModelAndView mavAuto = new ModelAndView();
		mavAuto.setViewName("listaAuto");
		mavAuto.addObject("auto", autoInSede);
		return mavAuto;

	}

	@GetMapping("/dettaglioauto")
	public ModelAndView dettaglioAuto(@RequestParam(name = "id") Integer id) {

		ModelAndView dettAuto = new ModelAndView();
		for (Auto a : autoInSede) {
			if (a.getId() == id) {
				dettAuto.setViewName("dettaglioauto");
				dettAuto.addObject("auto", a);
				return dettAuto;
			}

		}

		return null;
	}
}
