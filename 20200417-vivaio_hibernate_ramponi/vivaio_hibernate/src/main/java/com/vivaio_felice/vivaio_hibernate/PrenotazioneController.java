package com.vivaio_felice.vivaio_hibernate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vivaio_felice.vivaio_hibernate.dao.AutoDao;
import com.vivaio_felice.vivaio_hibernate.dao.CausaleDao;
import com.vivaio_felice.vivaio_hibernate.dao.CausaleNotificaDao;
import com.vivaio_felice.vivaio_hibernate.dao.NotificaDao;
import com.vivaio_felice.vivaio_hibernate.dao.ParcheggioDao;
import com.vivaio_felice.vivaio_hibernate.dao.PatenteDao;
import com.vivaio_felice.vivaio_hibernate.dao.PossessoPatentiDao;
import com.vivaio_felice.vivaio_hibernate.dao.PrenotazioneDao;

@Controller
public class PrenotazioneController {

	@Autowired
	AutoDao autoDao;
	@Autowired
	PrenotazioneDao prenotazioneDao;
	@Autowired
	CausaleDao causaleDao;
	@Autowired
	ParcheggioDao parcheggioDao;
	@Autowired
	PossessoPatentiDao posPatDao;
	@Autowired
	PatenteDao patDao;
	@Autowired
	NotificaDao notificaDao;
	@Autowired
	CausaleNotificaDao cauNotDao;

	// controllo tutti i giorni se ci sono km da inserire
	@Scheduled(cron = "0 0 18 * * ?")
	public void controlloKm() {

		for (Prenotazione p : prenotazioneDao.prenotazioniPerControlloKm(LocalDate.now().plus(1, ChronoUnit.DAYS))) {

			// la notifica si attiva se la prenotazione controllata è una prenotazione per
			// lavoro...
			if (p.getCausale().getId() == causaleDao.idLavoro()) {

				String avviso = "Devi inserire i km per una prenotazione!";
				notificaDao.save(new Notifica(p.getDipendente(), avviso, p, 0,
						cauNotDao.causaleDaInserire(cauNotDao.notPerKilometri())));

			}
			// ...e non se è una manutenzione, caso in cui si caricherebbero gli utlimi
			// km...
			if (p.getCausale().getId() != causaleDao.idLavoro()) {
				Prenotazione precedente = prenotazioneDao.precedente(p.getAuto().getId());
				if (precedente != null)
					p.setKm(precedente.getKm());
				// ...oppure i km iniziali se non sono mai state fatte prenotazioni
				if (precedente == null)
					p.setKm(p.getAuto().getKmIniziali());
				prenotazioneDao.save(p);

			}

		}

	}

	// metodo aggiornato per l'ottenimento auto
	public List<Auto> autoPrenotabili(List<Auto> autoNellaSede, boolean patC, boolean neoP, Integer idSede,
			LocalDateTime dataInizio, LocalDateTime dataFine) {

		// preparo la lista
		List<Auto> autoPrenotabili = new ArrayList<Auto>();

		for (Auto a : autoNellaSede)
			// controllo se ci sono prenotazioni dell'auto che contrastano...
			if (prenotazioneDao.prenoContrastanti(a.getId(), dataInizio, dataFine).isEmpty())
				// ... o trasferimenti analoghi
				if (parcheggioDao.trasferimentoContrastante(a.getId(), dataFine, idSede).isEmpty())
					// ultimi controlli sulla patente C o la neo patente prima di caricare in lista
					if ((!patC && a.getPatente().getId() == patDao.idB()) || patC)
						if (a.okForNeoP() || (!a.okForNeoP() && !neoP))
							autoPrenotabili.add(a);

		return autoPrenotabili;
	}

	@RequestMapping("/prenota")
	public String prenotazione(HttpSession session, Model model, Prenotazione prenotazione) {

		// se esiste questo attributo ma si arriva alla pagina di scelta date dal link è
		// bene cancellare l'attributo, altrimenti non si potrebbe fare le cose da zero
		// ma si riprenderebbe la prenotazione notificata
		if (session.getAttribute("nuova") != null) {
			session.removeAttribute("nuova");
		}
		return "prenota";
	}

	// cancello una prenotazione
	@RequestMapping("/cancella")
	public String cancellaPreno(HttpSession session, Model model, Prenotazione prenotazione) {

		Dipendente dip = (Dipendente) session.getAttribute("loggedUser");
		List<Prenotazione> prenotazioniDip = prenotazioneDao.prenotazioniDelDip(dip.getId(), LocalDateTime.now());
		model.addAttribute("prenotazioniDip", prenotazioniDip);

		return "cancellaPrenotazione";

	}

	@RequestMapping(value = "/prenoCancellata", method = RequestMethod.POST)
	public String cancellazioneEffettuata(HttpSession session, Model model, Prenotazione prenotazione) {

		// naturalmente quando cancello una prenotazione devo cancellare anche tutte le
		// notifiche relative, che non devono essere più mostrate
		List<Notifica> notDaCanc = notificaDao.notificheDaCancellare(prenotazione.getId());
		for (Notifica n : notDaCanc)
			notificaDao.delete(n);
		prenotazioneDao.delete(prenotazione);
		return "redirect:/primapagina";
	}

	@RequestMapping("/torna")
	public String ritorno(HttpSession session, Model model, Prenotazione prenotazione) {
		if (session.getAttribute("nuova") != null) {
			session.removeAttribute("nuova");
			return "redirect:/primapagina";
		}
		return "prenota";
	}

	@RequestMapping("/tornaKm")
	public String ritornoKm(HttpSession session, Model model, Prenotazione prenotazione) {
		return "redirect:/km";
	}

	@RequestMapping(value = "/richiestadiprenotazione", method = RequestMethod.POST)
	public String ottieniAuto(HttpSession session, Model model, @Valid Prenotazione prenotazione, BindingResult br,
			@RequestParam("dataInizio") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date dataInizio,
			@RequestParam("ore") Integer ore) {
		Dipendente loggato = (Dipendente) session.getAttribute("loggedUser");

		if (br.hasErrors())
			return "prenota";
		// la data di fine non viene inserita nel form ma si scelgono quante ore durerà
		// la prenotazione, ore poi aggiunte alla data di inizio per trovare quella
		// finale
		LocalDateTime ldtInizio = LocalDateTime.ofInstant(dataInizio.toInstant(), ZoneId.systemDefault());
		if (ldtInizio.isBefore(LocalDateTime.now())) {
			model.addAttribute("errore", "Hai inserito una data passata, riprova.");
			return "erroreMessaggio";
		}

		LocalDateTime ldtFine = ldtInizio.plus(ore, ChronoUnit.HOURS);
		ZonedDateTime zdtFine = ldtFine.atZone(ZoneId.systemDefault());
		Date dataFine = Date.from(zdtFine.toInstant());

		if (!prenotazioneDao.prenoContrastantiDelDipendente(loggato.getId(), ldtInizio, ldtFine).isEmpty()) {
			model.addAttribute("errore",
					"Attenzione, per le date selezionate hai già una prenotazione. Se vuoi annullarla usa la pagina apposita e poi creane una nuova.");
			return "erroreMessaggio";

		}

		// si trovano tutte le auto che si possono prenotare in base alle date e ai
		// booleani circa la patente c e il neo patentato
		Integer idSede = (Integer) session.getAttribute("sede");
		Dipendente d = (Dipendente) session.getAttribute("loggedUser");
		boolean patC = d.patenteC(posPatDao.findByDipendenteId(d.getId()), patDao.idC());
		boolean neoP = d.neoP(posPatDao.findByDipendenteId(d.getId()), patDao.idB());
		List<Auto> autoNellaSede = autoDao.autoInSede(idSede);
		List<Auto> autoPrenotabili = autoPrenotabili(autoNellaSede, patC, neoP, idSede, ldtInizio, ldtFine);
		model.addAttribute("autoDisponibili", autoPrenotabili);
		session.setAttribute("dataInizio", dataInizio);
		session.setAttribute("dataFine", dataFine);

		return "prenopossibili";

	}

	@RequestMapping(value = "/confermadiprenotazione", method = RequestMethod.POST)
	public String autoPrenotata(HttpSession session, Model model, Prenotazione prenotazione) {

		// quando si conferma una prenotazione arrivando dalla notifica
		if (session.getAttribute("nuova") != null) {
			Auto a = prenotazione.getAuto();
			prenotazione = (Prenotazione) session.getAttribute("nuova");
			prenotazione.setAuto(a);
			prenotazioneDao.save(prenotazione);

			Notifica n = notificaDao.notPren(prenotazione.getId());
			n.setConferma(1);
			notificaDao.save(n);

			session.removeAttribute("nuova");
			session.removeAttribute("dataInizio");
			session.removeAttribute("dataFine");
			return "redirect:/primapagina";
		}

		// quando si arriva dal link normale
		prenotazione.setDipendente((Dipendente) session.getAttribute("loggedUser"));
		prenotazione.setCausale(causaleDao.causaleDaId(causaleDao.idLavoro()));
		prenotazione.setDataInizio((Date) session.getAttribute("dataInizio"));
		prenotazione.setDataFine((Date) session.getAttribute("dataFine"));
		prenotazioneDao.save(prenotazione);

		session.removeAttribute("dataInizio");
		session.removeAttribute("dataFine");
		return "redirect:/primapagina";
	}

	@RequestMapping("/km")
	public String insKm(HttpSession session, Model model, Prenotazione prenotazione) {

		Dipendente d = (Dipendente) session.getAttribute("loggedUser");
		Integer idDip = d.getId();

		// recuperiamo l'ultima prenotazione di un dipendente se esiste, in modo da non
		// prendere quelle successive alla data odierna. La prenotazione PRECEDENTE si
		// riferisce alla prenotazione della stessa auto subito precedente all'ULTIMA
		Prenotazione precedente = null;
		Prenotazione ultima = prenotazioneDao.ultima(idDip, LocalDate.now().plus(1, ChronoUnit.DAYS));

		if (ultima != null)
			precedente = prenotazioneDao.precedente(ultima.getAuto().getId());

		session.setAttribute("ultima", ultima);
		session.setAttribute("precedente", precedente);
		return "inserimentoKm";
	}

	@RequestMapping(value = "/kminseriti", method = RequestMethod.POST)
	public String inseriti(HttpSession session, Model model, Prenotazione prenotazione) {

		Prenotazione ultima = (Prenotazione) session.getAttribute("ultima");
		Prenotazione precedente = (Prenotazione) session.getAttribute("precedente");

		Integer kmDiRiferimento = 0;

		// non si possono inserire km inferiori ai precedenti o agli iniziali
		if (precedente == null)
			kmDiRiferimento = ultima.getAuto().getKmIniziali();
		if (precedente != null)
			kmDiRiferimento = precedente.getKm();
		if (prenotazione.getKm() <= kmDiRiferimento) {
			// model per caricare un messaggio di errore, sarà presente in ogni post dov'è
			// possibile un errore
			// ritorna ad una pagina di erroe unica con un messaggio diverso per ogni errore
			model.addAttribute("errore", "Pare ci sia un errore nei km inseriti, controlla con attenzione!");
			return "erroreMessaggio";
		}

		// se si arriva da una notifica si conferma la notifica
		if (session.getAttribute("nuovap") != null) {
			Notifica n = notificaDao.notPren(ultima.getId());
			n.setConferma(1);
			notificaDao.save(n);
			session.removeAttribute("nuovap");
		}

		ultima.setKm(prenotazione.getKm());
		prenotazioneDao.save(ultima);

		session.removeAttribute("ultima");
		session.removeAttribute("precedente");

		return "redirect:/primapagina";

	}
}
