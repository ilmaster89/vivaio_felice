package com.vivaio_felice.vivaio_hibernate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
	CausaleNotificaDao causaleNotDao;

//	restituisce true se esiste una prenotazione successiva alle date della manutenzione, in modo da creare
//	le notifiche

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

	// effettuo una prenotazione per la manutenzione
	@RequestMapping(value = "/confermamanutenzione", method = RequestMethod.POST)
	public String confermaManu(HttpSession session, Model model, Prenotazione prenotazione,
			@Valid SpesaManutenzione spesaManutenzione, BindingResult br,
			@RequestParam("giornoInizio") @DateTimeFormat(pattern = "yyyy-MM-dd") Date giornoInizio,
			@RequestParam("giorni") Integer giorni) {

		// se non specifico nessun giorno torno alla stessa pagina ricaricando tutto ciò
		// che serve
		if (giornoInizio == null) {
			model.addAttribute("causali", causaleDao.causaliEccetto(causaleDao.idLavoro()));
			model.addAttribute("autoInSede",
					autoDao.autoInSede((Integer) session.getAttribute("sede"), LocalDate.now()));
			SpesaManutenzione sm = new SpesaManutenzione();
			model.addAttribute("spesaManutenzione", sm);

			return "manutenzione";
		}

		if (br.hasErrors()) {
			model.addAttribute("causali", causaleDao.causaliEccetto(causaleDao.idLavoro()));
			model.addAttribute("autoInSede",
					autoDao.autoInSede((Integer) session.getAttribute("sede"), LocalDate.now()));
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

		// due diverse date inizio, a seconda del tipo di manutenzione
		if (prenotazione.getCausale().getId() == causaleDao.idOrdinaria())
			dataInizio = giornoIntermedio;

		if (prenotazione.getCausale().getId() == causaleDao.idStraordinaria()) {
			dataInizio = Date
					.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plus(9, ChronoUnit.HOURS).toInstant());
			ldtGiornoInizio = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);

		}

		//uso una query per trovare se ci sono trasferimenti nel periodo della manutenzione
		List<Parcheggio> trasferimentiImpossibili = parcheggioDao.trasfPossibile(prenotazione.getAuto().getId(), dataInizio,
				dataFine);
		Integer sedeDip = (Integer) session.getAttribute("sede");
		
		for (Parcheggio p : trasferimentiImpossibili) {
			if(prenotazione.getCausale().getId() == causaleDao.idOrdinaria()) {
				//sposto di un giorno il trasferimento se la manutenzione è ordinaria
				LocalDate datap = p.getDataParch().plus(1, ChronoUnit.DAYS);
				p.setDataParch(datap);
				Notifica notAutoTrasf = new Notifica();
				notAutoTrasf.setConferma(0);
				//nuova query su dipendenteDao per il responsabile
				notAutoTrasf.setDipendente(dipendenteDao.respSede(sedeDip));
				notAutoTrasf.setDescrizione("Trasferimento dell'auto " + p.getAuto().toString() + " è stato spostato di un giorno per una manutenzione.");
				notAutoTrasf.setCausaleNotifica(causaleNotDao.causaleDaInserire(causaleNotDao.notGenerale()));
				notDao.save(notAutoTrasf);
			}
			if (prenotazione.getCausale().getId() == causaleDao.idStraordinaria()) {
				//se è straordinaria cancello il trasferimento
				Notifica notAutoTrasf = new Notifica();
				notAutoTrasf.setConferma(0);
				notAutoTrasf.setDipendente(dipendenteDao.respSede(sedeDip));
				notAutoTrasf.setDescrizione("Trasferimento dell'auto " + p.getAuto().toString() + " è stato cancellato per una manutenzione.");
				notAutoTrasf.setCausaleNotifica(causaleNotDao.causaleDaInserire(causaleNotDao.notGenerale()));
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
		List<Prenotazione> prenoAuto = prenotazioneDao.prenoDiUnPeriodo(prenotazione.getAuto().getId(), ldtGiornoInizio,
				ldtFine);

		for (Prenotazione p : prenoAuto) {

			Notifica not = new Notifica();
			not.setDipendente(p.getDipendente());
			not.setConferma(0);
			not.setPrenotazione(p);
			not.setDescrizione("Attenzione, la tua prenotazione per l'auto: " + p.getAuto().toString()
					+ ", prevista per queste date: " + p.getDataInizio() + " " + p.getDataFine()
					+ " deve essere modificata in quanto l'auto non sarà disponibile.");
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
