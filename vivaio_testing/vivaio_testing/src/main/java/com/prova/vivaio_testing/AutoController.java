package com.prova.vivaio_testing;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

@Controller
public class AutoController {

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
}
