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

	// dichiaro la ArrayList che servirà da contenitore per tutti i dipendenti.
	ArrayList<Dipendente> dipendenti = new ArrayList();

	@GetMapping("/")
	public String index(Dipendente dipendente) {
		return "index";
	}

	@PostMapping("/logged")
	public String loggedIn(Dipendente d) {

		// al controllo per il login, come avevo accennato, ho aggiunto il controllo per
		// l'id livello, che naturalmente porta a diverse pagine, come potrete
		// verificare da voi.
		// Una cosa curiosa su cui dovremo lavorare: se il controllo sull'id livello lo
		// faccio su "d", ovvero l'argomento passato al metodo, mi crea problemi. Se
		// invece lo faccio sul dipendente nell'ArrayList funziona perfettamente. Ci
		// lavoreremo, ma l'importante è che funzioni.
		for (Dipendente dip : VivaioTestingApplication.getDipendenti()) {
			if (d.getUser_name().equals(dip.getUser_name()) && d.getPassword().equals(dip.getPassword())
					&& dip.getId_livello() == 1)
				return "OperaiPrimaPagina";
			if (d.getUser_name().equals(dip.getUser_name()) && d.getPassword().equals(dip.getPassword())
					&& dip.getId_livello() == 2)
				return "Dipendenti_PrimaPagina";
			if (d.getUser_name().equals(dip.getUser_name()) && d.getPassword().equals(dip.getPassword())
					&& dip.getId_livello() == 3)
				return "Responsabile_PrimaPagina";
			if (d.getUser_name().equals(dip.getUser_name()) && d.getPassword().equals(dip.getPassword())
					&& dip.getId_livello() == 4)
				return "Responsabile_Prima_Pagina";

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

	@GetMapping("/IMPinsauto")
	public String insauto(Dipendente d) {
		return "Dipendenti_InserimentoAuto";
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
}
