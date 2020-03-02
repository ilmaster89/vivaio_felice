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

	// dichiaro la variabile lv come statica in modo da riprenderla ovunque
	public static int lv = 0;

	@GetMapping("/")
	public String index(Dipendente dipendente) {
		return "index";
	}

	@PostMapping("/logged")
	public String loggedIn(Dipendente d) {
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
		String query = "select id_livello from vivaio_felice.dipendenti where user_name = '" + d.getUser_name()
				+ "' and password = '" + d.getPassword() + "'";
		;
		ResultSet rs = null;
		try {
			rs = st.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// il resultset parte da una posizione "zero" ed è necessario usare il metodo
		// next per spostare il "puntatore" nell'effettiva tabella che ci è stata
		// restituita dalla query
		try {
			rs.next();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// comodissimo il metodo getInt che ci rende superfluo ogni tipo di casting.
		// Prendo l'int lv dall'unico campo che SQL ci ha restituito.
		try {
			lv = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// semplicissimo controllo sul dato ricevuto, non abbiamo creato alcun oggetto
		// dipendente e non abbiamo occupato memoria!
		if (lv == 1) {
			lv = 0;
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "OperaiPrimaPagina";
		}
		if (lv == 2) {
			lv = 0;
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "Dipendenti_PrimaPagina";
		}

		if (lv == 3 || lv == 4) {
			lv = 0;
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "Responsabile_PrimaPagina";
		}
		return "index";

	}

	@GetMapping("/lista")
	public ModelAndView lista() {

		ModelAndView mav = new ModelAndView();

		mav.setViewName("lista");
		mav.addObject("dipendenti", VivaioTestingApplication.getDipendenti());

		return mav;
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
		String query = "INSERT INTO `vivaio_felice`.`dipendenti` (`id_livello`, `nome`, `cognome`, `user_name`, `password`) VALUES ('"
				+ d.getId_livello() + "', '" + d.getNome() + "', '" + d.getCognome() + "', '" + d.getUser_name()
				+ "', '" + d.getPassword() + "')";
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

		return "redirect:/IMPinsdip";
	}
}
