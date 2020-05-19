package com.vivaio_felice.vivaio_hibernate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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

	@RequestMapping("/")
	public String index() {
		return "login";
	}

	@RequestMapping(value = "/logged", method = RequestMethod.POST)
	public String logged(@RequestParam("user") String user_name, @RequestParam("password") String password, Model model,
			HttpSession session) {

		Dipendente logged = dipendenteDao.login(user_name, password);

		if (logged == null)
			return "redirect:/";

		else {
			Integer idSede = sedeDipendenteDao.findByDipendenteId(logged.getId()).sede.getId();
			session.setAttribute("sede", idSede);
			session.setAttribute("loggedUser", logged);
			Sede questasede = sedeDao.sedeSingola(idSede);
			List<Auto> autoInSede = autoDao.autoInSede(idSede, LocalDate.now());

			for (Auto a : autoInSede) {

				if (parcheggioDao.parchDomani(a.getId(), LocalDate.now().plus(1, ChronoUnit.DAYS)) == null) {
					parcheggioDao.save(new Parcheggio(a, questasede, LocalDate.now().plus(1, ChronoUnit.DAYS)));
				}
			}

		}

		model.addAttribute("notifiche", notificaDao.notificheDip(logged.getId()));
		return "primapagina";
	}

	@RequestMapping("/notificaok/{id}")
	public String notOk(HttpSession session, Model model, Notifica notifica, @PathVariable("id") Integer id) {

		Notifica notDaCambiare = notificaDao.notDaId(id);
		notDaCambiare.setConferma(1);
		notificaDao.save(notDaCambiare);
		return "redirect:/primapagina";
	}

	@RequestMapping("/primapagina")
	public String primaPagina(HttpSession session, Model model, Dipendente dipendente) {

		dipendente = (Dipendente) session.getAttribute("loggedUser");
		Integer idDip = dipendente.getId();
		model.addAttribute("notifiche", notificaDao.notificheDip(idDip));

		return "primapagina";
	}

	@RequestMapping("/insdip")
	public String insdip(HttpSession session, Model model, Dipendente dipendente) {

		model.addAttribute("livello", livelloDao.treLivelli(livelloDao.capo()));

		return "inserimentoDipendenti";
	}

	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public String inserimento(Dipendente dipendente, Model model, HttpSession session) {

		dipendente.getSedeDipendente().setSede(sedeDao.sedeSingola((Integer) session.getAttribute("sede")));
		dipendente.getSedeDipendente().setDipendente(dipendente);

		dipendenteDao.save(dipendente);
		return "primapagina";
	}

	@RequestMapping("/inspat")
	public String inspat(HttpSession session, Model model, PossessoPatenti possessoPatenti) {

		Integer sede = (Integer) session.getAttribute("sede");

		model.addAttribute("dipendenti", sedeDipendenteDao.findBySedeId(sede));
		model.addAttribute("patenti", patenteDao.findAll());

		return "inserimentoPatente";

	}

	@RequestMapping(value = "/patenteInserita", method = RequestMethod.POST)
	public String patenteInserita(HttpSession session, Model model, PossessoPatenti possessoPatenti) {

		possPatDao.save(possessoPatenti);
		return "primapagina";

	}

	@RequestMapping("/indietro")
	public String indietro(HttpSession session, Model model, Dipendente dipendente) {

		return "redirect:/primapagina";
	}

}
