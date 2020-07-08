package com.vivaio_felice.vivaio_hibernate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vivaio_felice.vivaio_hibernate.dao.AutoDao;
import com.vivaio_felice.vivaio_hibernate.dao.CausaleDao;
import com.vivaio_felice.vivaio_hibernate.dao.CausaleNotificaDao;
import com.vivaio_felice.vivaio_hibernate.dao.DipendenteDao;
import com.vivaio_felice.vivaio_hibernate.dao.NotificaDao;
import com.vivaio_felice.vivaio_hibernate.dao.ParcheggioDao;
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
	@Autowired
	CausaleNotificaDao cauNotDao;
	@Autowired
	ParcheggioDao parcheggioDao;
	@Autowired
	DipendenteDao dipendenteDao;
	@Autowired
	MessageSource messageSource;

//	restituisce true se esiste una prenotazione successiva alle date della manutenzione, in modo da creare
//	le notifiche

	@RequestMapping("/manu")
	public String manu(HttpSession session, Model model, Prenotazione prenotazione,
			SpesaManutenzione spesaManutenzione) {

		// recupero tutte le causali di manutenzione e le auto in sede per scegliere
		// quella da manutenere
		model.addAttribute("causali", causaleDao.causaliEccetto(causaleDao.idLavoro()));
		model.addAttribute("autoInSede", autoDao.autoInSede((Integer) session.getAttribute("sede")));

		// carico una spesaManutenzione da affiancare alla prenotazione nel form, perché
		// alcuni campi fanno riferimento ad essa
		SpesaManutenzione sm = new SpesaManutenzione();
		model.addAttribute("spesaManutenzione", sm);

		return "manutenzione";
	}

	// effettuo una prenotazione per la manutenzione
	@RequestMapping(value = "/confermamanutenzione", method = RequestMethod.POST)
	public String confermaManu(HttpSession session, Model model, Prenotazione prenotazione,
			@Valid SpesaManutenzione spesaManutenzione, BindingResult br,
			@RequestParam("giornoInizio") @DateTimeFormat(pattern = "yyyy-MM-dd") Date giornoInizio,
			@RequestParam("giorni") Integer giorni, Locale loc) {

		// se non specifico nessun giorno torno alla stessa pagina ricaricando tutto ciò
		// che serve
		if (giornoInizio == null) {
			model.addAttribute("causali", causaleDao.causaliEccetto(causaleDao.idLavoro()));
			model.addAttribute("autoInSede", autoDao.autoInSede((Integer) session.getAttribute("sede")));
			SpesaManutenzione sm = new SpesaManutenzione();
			model.addAttribute("spesaManutenzione", sm);

			return "manutenzione";
		}

		if (br.hasErrors()) {
			model.addAttribute("causali", causaleDao.causaliEccetto(causaleDao.idLavoro()));
			model.addAttribute("autoInSede", autoDao.autoInSede((Integer) session.getAttribute("sede")));
			SpesaManutenzione sm = new SpesaManutenzione();
			model.addAttribute("spesaManutenzione", sm);

			return "manutenzione";
		}

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

		// calcolo un'eventuale data per il trasferimento
		LocalDateTime ldtTrasf = ldtFine.plus(1, ChronoUnit.DAYS);
		ZonedDateTime zdtTrasf = ldtTrasf.atZone(ZoneId.systemDefault());
		Date dataTrasf = Date.from(zdtTrasf.toInstant());

		// due diverse date inizio, a seconda del tipo di manutenzione
		if (prenotazione.getCausale().getId() == causaleDao.idOrdinaria())
			dataInizio = giornoIntermedio;

		if (prenotazione.getCausale().getId() == causaleDao.idStraordinaria()) {
			dataInizio = Date
					.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plus(9, ChronoUnit.HOURS).toInstant());
			ldtGiornoInizio = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);

		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Integer idSede = (Integer) session.getAttribute("sede");
		List<Parcheggio> trasferimentiImpossibili = parcheggioDao.trasfContrastanti(prenotazione.getAuto().getId(),
				dataInizio, dataFine, idSede);
		Integer sedeDip = (Integer) session.getAttribute("sede");

		for (Parcheggio p : trasferimentiImpossibili) {
			if (prenotazione.getCausale().getId() == causaleDao.idOrdinaria()) {
				// sposto di un giorno il trasferimento se la manutenzione è ordinaria
				LocalDate datap = p.getDataParch().plus(1, ChronoUnit.DAYS);
				p.setDataParch(datap);
				Notifica notAutoTrasf = new Notifica();
				notAutoTrasf.setConferma(0);
				// nuova query su dipendenteDao per il responsabile
				notAutoTrasf.setDipendente(dipendenteDao.respSede(sedeDip));
				notAutoTrasf.setDescrizione(messageSource.getMessage("manuTrasfUno", null, loc) + " "
						+ p.getAuto().toString() + " " + messageSource.getMessage("manuTrasfDue", null, loc) + "  "
						+ sdf.format(dataTrasf) + " " + messageSource.getMessage("manuTrasfTre", null, loc));
				notAutoTrasf.setCausaleNotifica(cauNotDao.causaleDaInserire(cauNotDao.notGenerale()));
				notDao.save(notAutoTrasf);
			}
			if (prenotazione.getCausale().getId() == causaleDao.idStraordinaria()) {
				// se è straordinaria cancello il trasferimento
				Notifica notAutoTrasf = new Notifica();
				notAutoTrasf.setConferma(0);
				notAutoTrasf.setDipendente(dipendenteDao.respSede(sedeDip));
				notAutoTrasf.setDescrizione(messageSource.getMessage("manuTrasfUno", null, loc) + " "
						+ p.getAuto().toString() + " " + messageSource.getMessage("manuTrasfCanc", null, loc));
				notAutoTrasf.setCausaleNotifica(cauNotDao.causaleDaInserire(cauNotDao.notGenerale()));
				parcheggioDao.delete(p);
				notDao.save(notAutoTrasf);
			}
		}
		prenotazione.setDataInizio(dataInizio);
		prenotazione.setDataFine(dataFine);
		prenotazione.setDipendente((Dipendente) session.getAttribute("loggedUser"));
		spesaManutenzione = (SpesaManutenzione) model.getAttribute("spesaManutenzione");
		spesaManutenzione.setAuto(prenotazione.getAuto());

		// genero le notifiche per le prenotazioni successive
		for (Prenotazione p : prenotazioneDao.prenoDiUnPeriodo(prenotazione.getAuto().getId(), ldtGiornoInizio,
				ldtFine)) {

			Notifica not = new Notifica();
			not.setDipendente(p.getDipendente());
			not.setConferma(0);
			not.setPrenotazione(p);
			not.setDescrizione(messageSource.getMessage("cambioPrenoUno", null, loc) + " " + p.getAuto()
					+ messageSource.getMessage("cambioPrenoDue", null, loc) + " " + p.getDataInizio() + " "
					+ p.getDataFine() + " " + messageSource.getMessage("cambioPrenoTre", null, loc));
			not.setCausaleNotifica(cauNotDao.causaleDaInserire(cauNotDao.notPerPrenotazione()));
			notDao.save(not);

		}

		prenotazioneDao.save(prenotazione);
		spesaManutenzioneDao.save(spesaManutenzione);

		return "redirect:/primapagina";
	}

	@RequestMapping("/spese")
	public String spese(HttpSession session, Model model, SpesaManutenzione spesaManutenzione) {

		// genero la lista delle spese ancora non inserite per farle scegliere al
		// dipendente. Relative alla sede
		Integer idSede = (Integer) session.getAttribute("sede");
		List<Auto> autoInSede = autoDao.autoInSede(idSede);
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

	// inserisco spesa e dettaglio per completare la manutenzione
	@RequestMapping(value = "/spesainserita", method = RequestMethod.POST)
	public String spesaInserita(HttpSession session, Model model, SpesaManutenzione spesaManutenzione) {

		// non posso andarmene senza inserire la spesa!
		if (spesaManutenzione.getSpesa() == null)
			return "spese";

		SpesaManutenzione spesaConfermata = spesaManutenzioneDao.spesaDaId(spesaManutenzione.getId());
		spesaConfermata.setDataSpesa(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		spesaConfermata.setSpesa(spesaManutenzione.getSpesa());
		spesaConfermata.setDettaglio(spesaManutenzione.getDettaglio());
		spesaManutenzioneDao.save(spesaConfermata);

		return "redirect:/spese";

	}
}
