package com.vivaio_felice.vivaio_hibernate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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
import com.vivaio_felice.vivaio_hibernate.dao.NotificaDao;
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
	@Autowired
	NotificaDao notDao;

//	restituisce true se esiste una prenotazione successiva alle date della manutenzione, in modo da creare
//	le notifiche
	public boolean prenoSuccessivaManu(Date dataInizio, Date dataFine, Date manu1, Date manu2) {

		if (PrenotazioneController.datesMatch(dataInizio, dataFine, manu1, manu2))
			return true;
		return false;

	}

	@RequestMapping("/manu")
	public String manu(HttpSession session, Model model, Prenotazione prenotazione,
			SpesaManutenzione spesaManutenzione) {

		// recupero tutte le causali di manutenzione e le auto in sede per scegliere
		// quella da manutenere
		model.addAttribute("causali", causaleDao.causaliEccetto(causaleDao.idLavoro()));
		model.addAttribute("autoInSede", autoDao.autoInSede((Integer) session.getAttribute("sede"), LocalDate.now()));

		// carico una spesaManutenzione da affiancare alla prenotazione nel form, perché
		// alcuni campi fanno riferimento ad essa
		SpesaManutenzione sm = new SpesaManutenzione();
		model.addAttribute("spesaManutenzione", sm);

		return "manutenzione";
	}

	@RequestMapping(value = "/confermamanutenzione", method = RequestMethod.POST)
	public String confermaManu(HttpSession session, Model model, Prenotazione prenotazione,
			SpesaManutenzione spesaManutenzione,
			@RequestParam("giornoInizio") @DateTimeFormat(pattern = "yyyy-MM-dd") Date giornoInizio,
			@RequestParam("giorni") Integer giorni) {

		// avanti di nove ore per inizio giornata
		LocalDateTime ldtGiornoInizio = LocalDateTime.ofInstant(giornoInizio.toInstant(), ZoneId.systemDefault());
		ldtGiornoInizio = ldtGiornoInizio.plus(9, ChronoUnit.HOURS);
		ZonedDateTime zdtGiornoInizio = ldtGiornoInizio.atZone(ZoneId.systemDefault());

		// dichiaro la data di inizio null perchè sarà decisa dagli if
		Date dataInizio = null;

		// si tratta del giorno preso dal form, che verrà usato come base per calcolare
		// la data fine
		Date giornoIntermedio = Date.from(zdtGiornoInizio.toInstant());
		LocalDateTime ldtIntermedia = LocalDateTime.ofInstant(giornoIntermedio.toInstant(), ZoneId.systemDefault());

		LocalDateTime ldtFine = ldtIntermedia.plus(9, ChronoUnit.HOURS);
		if (giorni > 1)
			ldtFine = ldtFine.plus(giorni - 1, ChronoUnit.DAYS);
		ZonedDateTime zdtFine = ldtFine.atZone(ZoneId.systemDefault());
		Date dataFine = Date.from(zdtFine.toInstant());

		// due diverse date inizio, a seconda del tipo di manutenzione
		if (prenotazione.getCausale().getId() == causaleDao.idOrdinaria())
			dataInizio = giornoIntermedio;

		if (prenotazione.getCausale().getId() == causaleDao.idStraordinaria())
			dataInizio = Date
					.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plus(9, ChronoUnit.HOURS).toInstant());

		prenotazione.setDataInizio(dataInizio);
		prenotazione.setDataFine(dataFine);
		prenotazione.setDipendente((Dipendente) session.getAttribute("loggedUser"));
		spesaManutenzione = (SpesaManutenzione) model.getAttribute("spesaManutenzione");
		spesaManutenzione.setAuto(prenotazione.getAuto());

		// genero le notifiche per le prenotazioni successive
		List<Prenotazione> prenoAuto = prenotazioneDao.findByAutoId(prenotazione.getAuto().getId());

		for (Prenotazione p : prenoAuto) {

			if (p.getCausale().getId() == causaleDao.idLavoro() && prenoSuccessivaManu(p.getDataInizio(),
					p.getDataFine(), prenotazione.getDataInizio(), prenotazione.getDataFine())) {
				Notifica not = new Notifica();
				not.setDipendente(p.getDipendente());
				not.setConferma(0);
				not.setPrenotazione(p);
				not.setDescrizione("Attenzione, la tua prenotazione per l'auto: " + p.getAuto().toString()
						+ ", prevista per queste date: " + p.getDataInizio() + " " + p.getDataFine()
						+ " deve essere modificata in quanto l'auto non sarà disponibile.");
				notDao.save(not);
			}

		}

		prenotazioneDao.save(prenotazione);
		spesaManutenzioneDao.save(spesaManutenzione);

		return "primapagina";
	}

	@RequestMapping("/spese")
	public String spese(HttpSession session, Model model, SpesaManutenzione spesaManutenzione) {

		// genero la lista delle spese ancora non inserite per farle scegliere al
		// dipendente. Relative alla sede
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
