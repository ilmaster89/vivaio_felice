package com.vivaio_felice.vivaio_hibernate;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vivaio_felice.vivaio_hibernate.dao.DipendenteDao;
import com.vivaio_felice.vivaio_hibernate.dao.DipendenteJdbcDao;

import com.vivaio_felice.vivaio_hibernate.dao.LivelloJdbcDao;
import com.vivaio_felice.vivaio_hibernate.dao.PatenteDao;
import com.vivaio_felice.vivaio_hibernate.dao.PossessoPatentiDao;
import com.vivaio_felice.vivaio_hibernate.dao.SedeDao;
import com.vivaio_felice.vivaio_hibernate.dao.SedeDipendenteDao;

@Controller

public class DipendenteController {

	@Autowired
	private DipendenteDao dipendenteDao;
	@Autowired
	private DipendenteJdbcDao dipendenteJdbcRepository;
	@Autowired
	private SedeDipendenteDao sedeDipendenteDao;

	@Autowired
	private LivelloJdbcDao livelloJdbcDao;
	@Autowired
	private PossessoPatentiDao possPatDao;
	@Autowired
	private PatenteDao patenteDao;

	@Autowired
	private SedeDao sedeDao;

	@RequestMapping("/")
	public String index() {
		return "login";
	}

	@RequestMapping(value = "/logged", method = RequestMethod.POST)
	public String logged(@RequestParam("user") String user_name, @RequestParam("password") String password, Model model,
			HttpSession session) {
		List<Dipendente> dipList = dipendenteJdbcRepository.login(user_name, password);

		if (dipList.size() == 0)
			return "redirect:/";
		else {
			SedeDipendente sedeDip = sedeDipendenteDao.findByDipendenteId(dipList.get(0).getId());
			Integer idSede = sedeDip.sede.getId();
			session.setAttribute("sede", idSede);
			session.setAttribute("loggedUser", dipList.get(0));

			return "primapagina";
		}
	}

	@RequestMapping("/primapagina")
	public String primaPagina() {
		return "primapagina";
	}

	@RequestMapping("/insdip")
	public String insdip(HttpSession session, Model model, Dipendente dipendente) {

		model.addAttribute("livello", livelloJdbcDao.treLivelli());

		return "inserimentoDipendenti";
	}

	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public String inserimento(Dipendente dipendente, Model model, HttpSession session) {
		Integer sede = (Integer) session.getAttribute("sede");

		Optional<Sede> miaSede = sedeDao.findById(sede);
		Sede questasede = miaSede.get();

		dipendente.getSedeDipendente().setSede(questasede);
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
	public String indietro() {
		return "primapagina";
	}

}