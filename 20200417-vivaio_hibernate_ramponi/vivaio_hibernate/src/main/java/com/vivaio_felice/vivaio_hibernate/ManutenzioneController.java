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
import com.vivaio_felice.vivaio_hibernate.dao.SpesaManutenzioneDao;

@Controller
public class ManutenzioneController {

	@Autowired
	SpesaManutenzioneDao spesaManutenzioneDao;
	@Autowired
	PrenotazioneDao prenotazioneDao;
	@Autowired
	AutoJdbcDao autoJdbcDao;
	@Autowired
	CausaleDao causaleDao;

	@RequestMapping("/manu")
	public String manu(HttpSession session, Model model, Prenotazione prenotazione,
			SpesaManutenzione spesaManutenzione) {

		List<Causale> manutenzioni = new ArrayList<Causale>();

		Causale c1 = causaleDao.findById(1).get();
		Causale c2 = causaleDao.findById(2).get();

		manutenzioni.add(c1);
		manutenzioni.add(c2);

		Integer idSede = (Integer) session.getAttribute("sede");
		List<Auto> autoInSede = autoJdbcDao.autoInSede(idSede);

		model.addAttribute("causali", manutenzioni);
		model.addAttribute("autoInSede", autoInSede);
		SpesaManutenzione sm = new SpesaManutenzione();
		model.addAttribute("spesaManutenzione", sm);

		return "manutenzione";
	}

	@RequestMapping(value = "/confermamanutenzione", method = RequestMethod.POST)
	public String confermaManu(HttpSession session, Model model, Prenotazione prenotazione,
			SpesaManutenzione spesaManutenzione,
			@RequestParam("dataInizio") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") Date dataInizio,
			@RequestParam("dataFine") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") Date dataFine) {

		prenotazione.setDataInizio(dataInizio);
		prenotazione.setDataFine(dataFine);
		prenotazione.setDipendente((Dipendente) session.getAttribute("loggedUser"));
		spesaManutenzione = (SpesaManutenzione) model.getAttribute("spesaManutenzione");
		spesaManutenzione.setAuto(prenotazione.getAuto());

		prenotazioneDao.save(prenotazione);
		spesaManutenzioneDao.save(spesaManutenzione);

		return "primapagina";
	}
}
