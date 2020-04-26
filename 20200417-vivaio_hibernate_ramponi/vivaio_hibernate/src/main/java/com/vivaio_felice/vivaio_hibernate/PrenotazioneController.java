package com.vivaio_felice.vivaio_hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vivaio_felice.vivaio_hibernate.dao.AutoJdbcDao;
import com.vivaio_felice.vivaio_hibernate.dao.CausaleDao;
import com.vivaio_felice.vivaio_hibernate.dao.PrenotazioneDao;

@Controller
public class PrenotazioneController {

	@SuppressWarnings("deprecation")
	public static boolean datesMatch(Date d1, Date d2, Date d3, Date d4) {

		if (d1.getYear() == d3.getYear() && d1.getMonth() == d3.getMonth() && d1.getDay() == d3.getDay()) {
			if (d1.getHours() == d3.getHours() && d1.getMinutes() == d3.getMinutes())
				return true;
			if (d2.getHours() == d4.getHours() && d2.getMinutes() == d4.getMinutes())
				return true;
			if (d1.after(d3) && d2.before(d4))
				return true;
			if (d1.before(d3) && d2.after(d4))
				return true;
			if ((d1.after(d3) && d1.before(d4)) && d2.after(d4))
				return true;
			if (d1.before(d3) && (d2.after(d3) && d2.before(d4)))
				return true;

		}

		return false;
	}

	public static Date d1 = null;
	public static Date d2 = null;

	@Autowired
	AutoJdbcDao autoJdbcDao;
	@Autowired
	PrenotazioneDao prenotazioneDao;
	@Autowired
	CausaleDao causaleDao;

	@RequestMapping("/prenota")
	public String prenotazione(HttpSession session, Model model, Prenotazione prenotazione) {
		d1 = null;
		d2 = null;
		return "prenota";
	}

	@RequestMapping(value = "/richiestadiprenotazione", method = RequestMethod.POST)
	public String ottieniAuto(HttpSession session, Model model, Prenotazione prenotazione,
			@RequestParam("dataInizio") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") Date dataInizio,
			@RequestParam("dataFine") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") Date dataFine) {

		d1 = dataInizio;
		d2 = dataFine;

		Integer idSede = (Integer) session.getAttribute("sede");
		List<Auto> autoNellaSede = autoJdbcDao.autoInSede(idSede);
		List<Prenotazione> prenotazioniSingolaAuto = new ArrayList<Prenotazione>();
		List<Auto> autoPrenotabili = new ArrayList<Auto>();

		for (Auto a : autoNellaSede) {
			prenotazioniSingolaAuto = prenotazioneDao.findByAutoId(a.getId());

			if (prenotazioniSingolaAuto.isEmpty())
				autoPrenotabili.add(a);

			else {
				boolean match = false;

				for (Prenotazione preno : prenotazioniSingolaAuto) {

					if (datesMatch(dataInizio, dataFine, preno.getDataInizio(), preno.getDataFine()))
						match = true;

				}

				if (!match)
					autoPrenotabili.add(a);
			}
		}

		model.addAttribute("autoDisponibili", autoPrenotabili);
		return "prenopossibili";

	}

	@RequestMapping(value = "/confermadiprenotazione", method = RequestMethod.POST)
	public String autoPrenotata(HttpSession session, Model model, Prenotazione prenotazione) {

		prenotazione.setDipendente((Dipendente) session.getAttribute("loggedUser"));
		prenotazione.setCausale(causaleDao.findById(3).get());
		prenotazione.setDataInizio(d1);
		prenotazione.setDataFine(d2);
		prenotazioneDao.save(prenotazione);
		d1 = null;
		d2 = null;
		return "primapagina";
	}

}
