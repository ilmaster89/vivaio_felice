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

	public static boolean datesMatch(Date d1, Date d2, Date d3, Date d4) {

		// partendo dal presupposto che il controllo si fa solo se il giorno è lo stesso
		if (d1.toInstant().compareTo(d3.toInstant()) == 0) {

			// queste sono le varie possibilità in cui le date cozzano
			if (d1.compareTo(d3) == 0)
				return true;
			if (d2.compareTo(d4) == 0)
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

	// vedi sopra, ma nel caso in cui esista un trasferimento per l'auto, che non
	// viene mostrata se prenotata per una data successiva al trasferimento stesso
	public static boolean datesMatchWithTrans(Date d1, Date d2, Date d3, Date d4, LocalDate trans) {

		ZoneId dzi = ZoneId.systemDefault();
		Instant inInizio = d1.toInstant();
		LocalDate ldInizio = inInizio.atZone(dzi).toLocalDate();
		Instant inFine = d2.toInstant();
		LocalDate ldFine = inFine.atZone(dzi).toLocalDate();

		if (ldInizio.compareTo(trans) >= 0 || ldFine.compareTo(trans) >= 0)
			return true;

		return datesMatch(d1, d2, d3, d4);

	}

	// carica le auto che effettivamente sono disponibili nelle date richieste,
	// tenenedo conto della possibilità che non vadano bene per i neo patentati o
	// per chi non ha la patente C
	public List<Auto> autoPossibili(List<Auto> autoNellaSede, boolean patC, boolean neoP, Integer idSede,
			Date dataInizio, Date dataFine) {

		LocalDate trans = null;
		List<Auto> autoPrenotabili = new ArrayList<Auto>();
		for (Auto a : autoNellaSede) {

			List<Prenotazione> prenotazioniSingolaAuto = prenotazioneDao.prenoAuto(a.getId());
			boolean transfer = false;
			boolean match = false;

			trans = parcheggioDao.ultimoParch(a.getId()).dataTrasferimento(idSede);
			if (trans != null)
				transfer = true;

			if (!prenotazioniSingolaAuto.isEmpty()) {
				for (Prenotazione p : prenotazioniSingolaAuto)
					if ((transfer
							&& datesMatchWithTrans(dataInizio, dataFine, p.getDataInizio(), p.getDataFine(), trans))
							|| (!transfer && datesMatch(dataInizio, dataFine, p.getDataInizio(), p.getDataFine())))
						match = true;

			}

			if ((!patC && a.getPatente().getId() == patDao.idB()) || patC)
				if (a.okForNeoP() || (!a.okForNeoP() && !neoP))
					if (!match)
						autoPrenotabili.add(a);

		}

		return autoPrenotabili;

	}

	@RequestMapping("/prenota")
	public String prenotazione(HttpSession session, Model model, Prenotazione prenotazione) {
		if (session.getAttribute("nuova") != null) {
			session.removeAttribute("nuova");
		}
		return "prenota";
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
	public String ottieniAuto(HttpSession session, Model model, Prenotazione prenotazione,
			@RequestParam("dataInizio") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date dataInizio,
			@RequestParam("ore") Integer ore) {

		// la data di fine non viene inserita nel form ma si scelgono quante ore durerà
		// la prenotazione, ore poi aggiunte alla data di inizio per trovare quella
		// finale
		LocalDateTime ldtInizio = LocalDateTime.ofInstant(dataInizio.toInstant(), ZoneId.systemDefault());
		LocalDateTime ldtFine = ldtInizio.plus(ore, ChronoUnit.HOURS);
		ZonedDateTime zdtFine = ldtFine.atZone(ZoneId.systemDefault());
		Date dataFine = Date.from(zdtFine.toInstant());

		if (dataFine.compareTo(dataInizio) <= 0
				|| dataInizio.before(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
			// model per caricare un messaggio di errore, sarà presente in ogni post dov'è
			// possibile un errore
			// ritorna ad una pagina di erroe unica con un messaggio diverso per ogni errore
			model.addAttribute("errore", "le date sono sbagliate");
			return "erroreMessaggio";
		}

		// non si possono prenotare auto per una data precedente ad oggi!
		if (dataFine.compareTo(dataInizio) <= 0
				|| dataInizio.before(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())))
			return "erroreData";

		Integer idSede = (Integer) session.getAttribute("sede");
		Dipendente d = (Dipendente) session.getAttribute("loggedUser");
		boolean patC = d.patenteC(posPatDao.findByDipendenteId(d.getId()), patDao.idC());
		boolean neoP = d.neoP(posPatDao.findByDipendenteId(d.getId()), patDao.idC());
		List<Auto> autoNellaSede = autoDao.autoInSede(idSede, LocalDate.now());
		List<Auto> autoPrenotabili = autoPossibili(autoNellaSede, patC, neoP, idSede, dataInizio, dataFine);
		model.addAttribute("autoDisponibili", autoPrenotabili);
		session.setAttribute("dataInizio", dataInizio);
		session.setAttribute("dataFine", dataFine);

		return "prenopossibili";

	}

	@RequestMapping(value = "/confermadiprenotazione", method = RequestMethod.POST)
	public String autoPrenotata(HttpSession session, Model model, Prenotazione prenotazione) {

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
		prenotazione.setDipendente((Dipendente) session.getAttribute("loggedUser"));
		prenotazione.setCausale(causaleDao.causaleDaId(causaleDao.idLavoro()));
		prenotazione.setDataInizio((Date) session.getAttribute("dataInizio"));
		prenotazione.setDataFine((Date) session.getAttribute("dataFine"));
		prenotazioneDao.save(prenotazione);

		session.removeAttribute("dataInizio");
		session.removeAttribute("dataFine");
		return "primapagina";
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

		if (precedente != null && prenotazione.getKm() <= precedente.getKm()
				&& ultima.getDataFine().after(precedente.getDataFine())) {
			// model per caricare un messaggio di errore, sarà presente in ogni post dov'è
			// possibile un errore
			// ritorna ad una pagina di erroe unica con un messaggio diverso per ogni errore
			model.addAttribute("errore", "i km inseriti non corrispondono al vero, vedi di fare il serio");
			return "erroreMessaggio";
		}

		// i km devono essere successivi agli ultimi inseriti
		if (precedente != null && prenotazione.getKm() <= precedente.getKm()
				&& ultima.getDataFine().after(precedente.getDataFine()))
			return "erroreKm";

		ultima.setKm(prenotazione.getKm());
		prenotazioneDao.save(ultima);

		session.removeAttribute("ultima");
		session.removeAttribute("precedente");

		return "primapagina";

	}
}
