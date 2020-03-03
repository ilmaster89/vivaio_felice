package com.prova.vivaio_testing;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

@Controller
public class AutoController {

	ArrayList<AutoShort> autoInSede = new ArrayList();

	@GetMapping("/IMPinsauto")
	public String insauto(Auto a) {
		return "Dipendenti_InserimentoAuto";
	}

	@PostMapping("/insertAuto")
	public String autoInserita(Auto a) {
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

		// query che prevede come filtri i due campi riempiti nel form della pagina
		String query = "INSERT INTO `vivaio_felice`.`auto` (`id_patente`, `carburante`, `marca`, `modello`, `targa`, `kw`, `tara`, `data_assicurazione`) "
				+ "VALUES ('" + a.getIdPatente() + "', '" + a.getCarburante() + "', '" + a.getMarca() + "', '"
				+ a.getModello() + "', '" + a.getTarga() + "', '" + a.getKw() + "', '" + a.getTara() + "', '"
				+ a.getDataAssicurazione() + "')";
		;
		try {
			st.executeUpdate(query);
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

	@GetMapping("/listaAUTO")
	public ModelAndView listaAuto(Dipendente d) {
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

		String query = "select marca, modello, targa from parcheggio as p1 "
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
				String marca = rs.getString(1);
				String modello = rs.getString(2);
				String targa = rs.getString(3);

				AutoShort a = new AutoShort(marca, modello, targa);
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
}
