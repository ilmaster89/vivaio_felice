package com.vivaio_felice.vivaio_hibernate;

import java.time.LocalDate;

import java.util.ArrayList;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vivaio_felice.vivaio_hibernate.dao.AutoDao;
import com.vivaio_felice.vivaio_hibernate.dao.DipendenteDao;
import com.vivaio_felice.vivaio_hibernate.dao.LivelloDao;
import com.vivaio_felice.vivaio_hibernate.dao.NotificaDao;
import com.vivaio_felice.vivaio_hibernate.dao.ParcheggioDao;
import com.vivaio_felice.vivaio_hibernate.dao.PatenteDao;
import com.vivaio_felice.vivaio_hibernate.dao.PossessoPatentiDao;
import com.vivaio_felice.vivaio_hibernate.dao.PrenotazioneDao;
import com.vivaio_felice.vivaio_hibernate.dao.SedeDao;
import com.vivaio_felice.vivaio_hibernate.dao.SedeDipendenteDao;

@Controller

public class DipendenteController {

	@Autowired
	private DipendenteDao dipendenteDao;
	@Autowired
	private SedeDipendenteDao sedeDipendenteDao;
	@Autowired
	private LivelloDao livelloDao;
	@Autowired
	private PossessoPatentiDao possPatDao;
	@Autowired
	private PatenteDao patenteDao;
	@Autowired
	ParcheggioDao parcheggioDao;
	@Autowired
	private SedeDao sedeDao;
	@Autowired
	NotificaDao notificaDao;
	@Autowired
	AutoDao autoDao;
	@Autowired
	PrenotazioneDao prenoDao;

	@RequestMapping("/")
	public String index() {
		return "login";
	}

	@RequestMapping(value = "/logged", method = RequestMethod.POST)
	public String logged(@RequestParam("user") String user_name, @RequestParam("password") String password, Model model,
			HttpSession session) {

		Dipendente logged = dipendenteDao.login(user_name, password);
		List<Notifica> notifiche = new ArrayList<Notifica>();

		if (logged == null)
			return "redirect:/";

		else {
			Integer idSede = sedeDipendenteDao.findByDipendenteId(logged.getId()).sede.getId();
			session.setAttribute("sede", idSede);
			session.setAttribute("loggedUser", logged);

//			prendiamo tutte le auto in sede e, se non sono già state
//			confermate o vengono trasferite il giorno dopo, si salvano
//			i parcheggi del giorno successivo
			List<Auto> autoInSede = autoDao.autoInSede(idSede, LocalDate.now());

			for (Auto a : autoInSede) {

//				se entra un livello due o tre vengono mostrate anche eventuali notifiche
//				riguardanti le auto della sede, ma non specifiche per dipendente
				if (logged.impiegato() || logged.responsabile())
					if (notificaDao.notificheAuto(a.getId()) != null)
						notifiche.add(notificaDao.notificheAuto(a.getId()));

			}
		}

		notifiche.addAll(notificaDao.notificheDip(logged.getId()));
		model.addAttribute("notifiche", notifiche);

		return "primapagina";

	}

// ogni volta che si torna indietro si richiama questo metodo, così
// che appaiano tutte le notifiche sempre finché non vengono
// confermate
	@RequestMapping("/primapagina")
	public String primaPagina(HttpSession session, Model model, Dipendente dipendente) {

		Integer idSede = (Integer) session.getAttribute("sede");
		List<Auto> autoInSede = autoDao.autoInSede(idSede, LocalDate.now());
		List<Notifica> notifiche = new ArrayList<Notifica>();
		dipendente = (Dipendente) session.getAttribute("loggedUser");
		Integer idDip = dipendente.getId();
		for (Auto a : autoInSede)
			if (dipendente.impiegato() || dipendente.responsabile())
				if (notificaDao.notificheAuto(a.getId()) != null)
					notifiche.add(notificaDao.notificheAuto(a.getId()));
		notifiche.addAll(notificaDao.notificheDip(idDip));
		model.addAttribute("notifiche", notifiche);

		return "primapagina";
	}

//	recupero tutti i livelli tranne il titolare per l'inserimento
	@RequestMapping("/insdip")
	public String insdip(HttpSession session, Model model, Dipendente dipendente) {

		model.addAttribute("livello", livelloDao.treLivelli(livelloDao.capo()));

		return "inserimentoDipendenti";
	}

	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public String inserimento(HttpSession session, Model model, @Valid Dipendente dipendente,
			BindingResult bindingResult, @RequestParam("password") String password1,
			@RequestParam("pass2") String password2) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("livello", livelloDao.treLivelli(livelloDao.capo()));
			return "inserimentoDipendenti";

		}

		if (password1.equals(password2)) {
			dipendente.getSedeDipendente().setSede(sedeDao.sedeSingola((Integer) session.getAttribute("sede")));
			dipendente.getSedeDipendente().setDipendente(dipendente);

			dipendenteDao.save(dipendente);
		} else {
			// model per caricare un messaggio di errore, sarà presente in ogni post dov'è
			// possibile un errore
			// ritorna ad una pagina di errore unica con un messaggio diverso per ogni
			// errore
			model.addAttribute("errore", "La password è errata, reinserire il dipendente");
			return "erroreMessaggio";// redirect a pagina di errore unica
		}
		return "redirect:/primapagina";
	}

//	recupero i dipendenti in sede e le patenti per l'inserimento di una 
//	nuova patente
	@RequestMapping("/inspat")
	public String inspat(HttpSession session, Model model, PossessoPatenti possessoPatenti) {

		Integer sede = (Integer) session.getAttribute("sede");

		model.addAttribute("dipendenti", sedeDipendenteDao.findBySedeId(sede));
		model.addAttribute("patenti", patenteDao.findAll());

		return "inserimentoPatente";

	}

	@RequestMapping(value = "/patenteInserita", method = RequestMethod.POST)
	public String patenteInserita(HttpSession session, Model model, @Valid PossessoPatenti possessoPatenti,
			BindingResult br) {
		if (br.hasErrors()) {
			Integer sede = (Integer) session.getAttribute("sede");

			model.addAttribute("dipendenti", sedeDipendenteDao.findBySedeId(sede));
			model.addAttribute("patenti", patenteDao.findAll());
			return "inserimentoPatente";
		}
		List<PossessoPatenti> possPatDip = possPatDao.findByDipendenteId(possessoPatenti.getDipendente().getId());
		for (PossessoPatenti a : possPatDip) {
			if (a.getPatente() == possessoPatenti.getPatente()) {
				model.addAttribute("errore", "Questo dipendente ha già una patente di questo tipo, riprova.");
				return "erroreMessaggio";
			}
		}
		possPatDao.save(possessoPatenti);
		return "primapagina";

	}

	@RequestMapping("/indietro")
	public String indietro(HttpSession session, Model model, Dipendente dipendente) {

		return "redirect:/primapagina";
	}

}
