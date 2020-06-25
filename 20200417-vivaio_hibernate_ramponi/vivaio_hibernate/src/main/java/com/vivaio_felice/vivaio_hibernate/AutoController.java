package com.vivaio_felice.vivaio_hibernate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vivaio_felice.vivaio_hibernate.dao.AutoDao;
import com.vivaio_felice.vivaio_hibernate.dao.CarburanteDao;
import com.vivaio_felice.vivaio_hibernate.dao.CausaleNotificaDao;
import com.vivaio_felice.vivaio_hibernate.dao.NotificaDao;
import com.vivaio_felice.vivaio_hibernate.dao.ParcheggioDao;
import com.vivaio_felice.vivaio_hibernate.dao.PatenteDao;
import com.vivaio_felice.vivaio_hibernate.dao.PrenotazioneDao;
import com.vivaio_felice.vivaio_hibernate.dao.SedeDao;

@Controller
public class AutoController {

	@Autowired
	AutoDao autoDao;
	@Autowired
	ParcheggioDao parcheggioDao;
	@Autowired
	PatenteDao patenteDao;
	@Autowired
	CarburanteDao carburanteDao;
	@Autowired
	PrenotazioneDao prenoDao;
	@Autowired
	SedeDao sedeDao;
	@Autowired
	NotificaDao notificaDao;
	@Autowired
	CausaleNotificaDao cauNotDao;

//	Ogni lunedì alle 9 viene controllata la data di assicurazione
//	di tutte le auto nel parco, in modo da generare eventuali 
//	notifiche se la scadenza è entro il mese
	@Scheduled(cron = "0 0 9 * * MON")
	public void assicurazioniInScadenza() {

		LocalDate traUnMese = LocalDate.now().plus(1, ChronoUnit.MONTHS);
		for (Auto a : autoDao.findAll()) {

			LocalDate dataAss = a.getDataAss().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			// la scadenza è dopo un anno dalla data segnata
			LocalDate scadenza = dataAss.plus(1, ChronoUnit.YEARS);

			// confronto scadenza e data un mese da oggi per poter valutare se
			// l'assicurazione scade entro il mese
			if (scadenza.compareTo(traUnMese) <= 0) {

				String avviso = "L'assicurazione dell'auto: " + a.toString() + " scadrà il " + scadenza
						+ ", ricordati di rinnovarla!";

				notificaDao
						.save(new Notifica(avviso, a, 0, cauNotDao.causaleDaInserire(cauNotDao.notPerAssicurazione())));
			}

		}

	}

	// metodo schedulato per caricare le auto nel parcheggio piuttosto che farle
	// caricare al login del dipendente
	@Scheduled(cron = "0 54 9 * * ?")
	public void confermaParcheggi() {

		// carico le auto che sono in parcheggio OGGI
		for (Auto a : autoDao.autoParcheggiate(LocalDate.now())) {

			// preparo la sede se devo aggiungerla al costruttore
			Sede sede = sedeDao.sedeSingola(parcheggioDao.sedeOdierna(a.getId(), LocalDate.now()));

			// carico gli eventuali parcheggi già confermati
			Parcheggio domani = parcheggioDao.parchDomani(a.getId(), LocalDate.now().plus(1, ChronoUnit.DAYS));

			// se non ci sono parcheggi per la data di domani li creo
			if (domani == null) {
				parcheggioDao.save(new Parcheggio(a, sede, LocalDate.now().plus(1, ChronoUnit.DAYS)));
			}
		}

	}

	@RequestMapping("/insauto")
	public String insAuto(HttpSession session, Model model, Auto auto) {

		// caricamento di variabili per il select corrispondente nella pagina del sito
		model.addAttribute("carburanti", carburanteDao.findAll());
		model.addAttribute("patenti", patenteDao.findAll());
		return "inserimentoAuto";
	}

	@RequestMapping(value = "/insertAuto", method = RequestMethod.POST)
	public String autoInserita(HttpSession session, Model model, @Valid Auto auto, BindingResult bindingResult,
			Parcheggio parcheggio) {

		if (bindingResult.hasErrors()) {
			// se devo tornare alla stessa pagina mi servono ancora i due select
			model.addAttribute("carburanti", carburanteDao.findAll());
			model.addAttribute("patenti", patenteDao.findAll());
			return "inserimentoAuto";
		}

		Sede questasede = sedeDao.sedeSingola((Integer) session.getAttribute("sede"));
		autoDao.save(auto);

		// dopo aver inserito un'auto si salvano in automatico anche il parcheggio
		// ODIERNO e quello di DOMANI
		parcheggioDao.save(new Parcheggio(auto, questasede, LocalDate.now()));
		parcheggioDao.save(new Parcheggio(auto, questasede, LocalDate.now().plus(1, ChronoUnit.DAYS)));
		return "redirect:/primapagina";

	}

	@RequestMapping(value = "/assRinnovata", method = RequestMethod.POST)
	public String rinnovo(HttpSession session, Model model, @Valid Auto auto, BindingResult br) {

		if (br.hasErrors())
			return "rinnovoAssicurazione";

		// sostituisto la data presente nell'auto caricata in sessione con quella
		// dell'auto argomento del metodo
		Date nuovaAss = auto.getDataAss();
		auto = (Auto) session.getAttribute("autoDaRinnovare");
		auto.setDataAss(nuovaAss);
		autoDao.save(auto);

		// cambio la conferma alla notifica così che non venga più mostrata
		Notifica confermata = notificaDao.notificheAuto(auto.getId());
		confermata.setConferma(1);
		notificaDao.save(confermata);
		session.removeAttribute("autoDaRinnovare");
		return "redirect:/primapagina";

	}

	@RequestMapping("/modifica")
	public String modificaAuto(HttpSession session, Model model, Auto auto) {

		// carico le auto per scegliere quella che va modificata
		List<Auto> autosede = autoDao.autoInSede((Integer) session.getAttribute("sede"), LocalDate.now());
		model.addAttribute("autosede", autosede);
		return "modAuto";
	}

	@RequestMapping(value = "/modificheAuto", method = RequestMethod.POST)
	public String autoScelta(HttpSession session, Model model, Auto auto) {

		// carico l'auto scelta e le variabili necessarie per i select
		Auto autoDaModificare = autoDao.findByTarga(auto.getTarga());
		session.setAttribute("autoDaModificare", autoDaModificare);
		model.addAttribute("carburanti", carburanteDao.findAll());
		model.addAttribute("patenti", patenteDao.findAll());
		return "modificaAuto";
	}

	@RequestMapping(value = "/autoModificata", method = RequestMethod.POST)
	public String autoDaModificare(HttpSession session, Model model, @Valid Auto auto, BindingResult br) {
		if (br.hasErrors()) {
			model.addAttribute("errore", "Non hai inserito alcuni valori, riprova");
			return "erroreMessaggio";
		}

		// sostituisco le caratteristiche dell'auto in sessione con quelle che ho
		// inserito nel form
		Auto autoCambiata = (Auto) session.getAttribute("autoDaModificare");
		autoCambiata.setPatente(auto.getPatente());
		autoCambiata.setCarburante(auto.getCarburante());
		autoCambiata.setMarca(auto.getMarca());
		autoCambiata.setModello(auto.getModello());
		autoCambiata.setTarga(auto.getTarga());
		autoCambiata.setKw(auto.getKw());
		autoCambiata.setTara(auto.getTara());
		autoCambiata.setDataAss(auto.getDataAss());
		autoCambiata.setKmIniziali(auto.getKmIniziali());
		autoDao.save(autoCambiata);
		return "redirect:/primapagina";
	}

	@RequestMapping("/cancAuto")
	public String cancellaAuto(HttpSession session, Model model, Auto auto) {
		List<Auto> autoCancellabili = autoDao.autoInSede((Integer) session.getAttribute("sede"), LocalDate.now());
		model.addAttribute("autoCancellabili", autoCancellabili);
		return "cancellaAuto";
	}

	// con il post setto a 1 la disponibilità, ovvero a false
	@RequestMapping(value = "/autoCancellata", method = RequestMethod.POST)
	public String autoEliminata(HttpSession session, Model model, @RequestParam("idAuto") Integer idAuto, Auto auto) {
		Auto autoCanc = autoDao.autoDaId(idAuto);
		// ho modificato anche le query in autoDao mettendoci la condizione sulla
		// disponibilità
		// ho aggiunto il controllo sulla disponibilita anche nel metodo auto possibili
		// sul controller delle preno e sulle notifiche
		autoCanc.setDisponibilita(1);
		autoDao.save(autoCanc);
		// creo una lista di prenotazioni con quell'auto cancellata
		List<Prenotazione> prenoAutoC = prenoDao.findByAutoId(idAuto);
		for (Prenotazione p : prenoAutoC) {
			// controllo se la data è successiva o è di oggi, per far sì che i dipendenti
			// possano modificarla
			if ((p.getDataInizio().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
					.compareTo(LocalDate.now()) >= 0) {
				Notifica n = new Notifica();
				n.setDipendente(p.getDipendente());
				n.setDescrizione("Auto eliminata, modifica la prenotazione");
				n.setPrenotazione(p);
				n.setConferma(0);
				n.setCausaleNotifica(cauNotDao.causaleDaInserire(cauNotDao.notPerPrenotazione()));
				notificaDao.save(n);
			}
		}
		return "redirect:/primapagina";
	}
}
