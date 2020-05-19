package com.vivaio_felice.vivaio_hibernate;

import java.time.LocalDate;
import java.time.ZoneId;
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

import com.vivaio_felice.vivaio_hibernate.dao.AutoDao;
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
	AutoDao autoDao;
	@Autowired
	CausaleDao causaleDao;

	@RequestMapping("/manu")
	public String manu(HttpSession session, Model model, Prenotazione prenotazione,
			SpesaManutenzione spesaManutenzione) {

		model.addAttribute("causali", causaleDao.causaliEccetto(causaleDao.idLavoro()));
		model.addAttribute("autoInSede", autoDao.autoInSede((Integer) session.getAttribute("sede"), LocalDate.now()));

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

	@RequestMapping("/spese")
	public String spese(HttpSession session, Model model, SpesaManutenzione spesaManutenzione) {

		Integer idSede = (Integer) session.getAttribute("sede");
		List<Auto> autoInSede = autoDao.autoInSede(idSede, LocalDate.now());
		List<SpesaManutenzione> speseNonConfermate = new ArrayList<SpesaManutenzione>();

		for (Auto a : autoInSede) {
			List<SpesaManutenzione> speseAuto = spesaManutenzioneDao.manutenzioniSede(a.getId());

			if (!speseAuto.isEmpty())
				for (SpesaManutenzione s : speseAuto)
					speseNonConfermate.add(s);
		}

		model.addAttribute("speseDaConfermare", speseNonConfermate);
		return "spese";

	}

	@RequestMapping(value = "/spesainserita", method = RequestMethod.POST)
	public String spesaInserita(HttpSession session, Model model, SpesaManutenzione spesaManutenzione) {

		SpesaManutenzione spesaConfermata = spesaManutenzioneDao.spesaDaId(spesaManutenzione.getId());
		spesaConfermata.setDataSpesa(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		spesaConfermata.setSpesa(spesaManutenzione.getSpesa());
		spesaConfermata.setDettaglio(spesaManutenzione.getDettaglio());
		spesaManutenzioneDao.save(spesaConfermata);

		return "redirect:/spese";

	}
}
