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

	@GetMapping("/IMPinsauto")
	public String insauto(Auto a) {
		return "Dipendenti_InserimentoAuto";
	}

	// inserimento auto.
	@PostMapping("/insertAuto")
	public String autoInserita(Auto a) {
		Connection conn = null;
		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/vivaio_felice", "root",
					"password");
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

		// questo update inserisce i valori nella tabella AUTO del nostro DB.
		String update1 = "INSERT INTO `vivaio_felice`.`auto` (`id_patente`, `carburante`, `marca`, `modello`, `targa`, `kw`, `tara`, `data_assicurazione`) "
				+ "VALUES ('" + a.getIdPatente() + "', '" + a.getCarburante() + "', '" + a.getMarca() + "', '"
				+ a.getModello() + "', '" + a.getTarga() + "', '" + a.getKw() + "', '" + a.getTara() + "', '"
				+ a.getDataAssicurazione() + "')";

		// questa piccola query è essenziale per recuperare l'id dell'ultima auto in
		// tabella, ovvero quella che stiamo inserendo in questo istante.
		String idAuto = "select id from auto order by id desc limit 1";

		// preparo la variabile id a cui poi associerò l'int trovato tramite la query
		// sopra.
		int idNuovaAuto = 0;
		try {

			// ATTENZIONE: per inserire nuovi dati uso execute UPDATE e non QUERY.
			st.executeUpdate(update1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ResultSet rs = null;
		try {
			rs = st.executeQuery(idAuto);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// modifico la variabile id auto con il risultato ottenuto.
		try {
			rs.next();
			idNuovaAuto = rs.getInt(1);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// con questo importantissimo update vado ad inserire la nuova auto nel
		// parcheggio, usando importanti variabili come clausole where. l'idNuovaAuto lo
		// prendo da sopra, la sede la prendo dal DipendenteController dove l'avevo
		// dichiarata, era statica quindi basta chiamarla con il punto. Infine, uso
		// LocalDate.now() in modo che nella tabella venga inserita la data odierna come
		// data_parch. Fondamentale inserire questi dati nella tabella, perché saranno
		// poi essenziali per andare a selezionare le auto in sede.
		String update2 = "INSERT INTO `vivaio_felice`.`parcheggio` (`id_auto`, `id_sede`, `data_parch`) VALUES ('"
				+ idNuovaAuto + "', '" + DipendenteController.sede + "', '" + LocalDate.now() + "');";
		try {
			st.executeUpdate(update2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "redirect:/IMPinsauto";
	}

	// lista delle auto in sede. Funziona esattamente come la lista dei dipendenti,
	// con le ovvie differenze relative alla tabella e ai dati da recuperare.
	@GetMapping("/listaAUTO")
	public ModelAndView listaAuto(Dipendente d) {

		autoInSede = new ArrayList();
		Connection conn = null;
		try {
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/vivaio_felice", "root",
					"password");
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

		String query = "select auto.id, id_patente,carburante, marca, modello, targa, kw, tara, data_assicurazione from parcheggio as p1 "
				+ "left outer join parcheggio as p2 on p1.id_auto=p2.id_auto and p1.data_parch < p2.data_parch "
				+ "join auto on p1.id_auto = auto.id " + "join sede on sede.id = p1.id_sede "
				+ "where p2.id is null and p1.id_sede = " + DipendenteController.sede;

		ResultSet rs = null;
		try {
			rs = st.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
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
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
