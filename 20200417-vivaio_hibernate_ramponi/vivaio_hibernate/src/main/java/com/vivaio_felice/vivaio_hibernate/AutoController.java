package com.vivaio_felice.vivaio_hibernate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vivaio_felice.vivaio_hibernate.dao.AutoDao;
import com.vivaio_felice.vivaio_hibernate.dao.AutoJdbcDao;
import com.vivaio_felice.vivaio_hibernate.dao.CarburanteDao;
import com.vivaio_felice.vivaio_hibernate.dao.ParcheggioDao;
import com.vivaio_felice.vivaio_hibernate.dao.PatenteDao;
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
	AutoJdbcDao autoJdbcDao;
	@Autowired
	SedeDao sedeDao;

	@RequestMapping("/insauto")
	public String insAuto(HttpSession session, Model model, Auto auto) {

		model.addAttribute("carburanti", carburanteDao.findAll());
		model.addAttribute("patenti", patenteDao.findAll());
		return "inserimentoAuto";
	}

	@RequestMapping(value = "/insertAuto", method = RequestMethod.POST)
	public String autoInserita(HttpSession session, Model model, Auto auto, Parcheggio parcheggio) {

		Integer idSede = (Integer) session.getAttribute("sede");
		Sede questasede = sedeDao.findById(idSede).get();
		parcheggio.setSede(questasede);
		parcheggio.setDataParch(LocalDate.now());
		parcheggio.setAuto(auto);

		autoDao.save(auto);
		parcheggioDao.save(parcheggio);
		return "primapagina";

	}

	@RequestMapping("/insparch")
	public String insParch(HttpSession session, Model model, Parcheggio parcheggio) {

		Integer idSede = (Integer) session.getAttribute("sede");
		List<Auto> autoInSede = autoJdbcDao.autoInSede(idSede);
		List<Auto> autoDaConfermare = new ArrayList<Auto>();
		List<Parcheggio> parcheggiAuto = new ArrayList<Parcheggio>();

		for (Auto a : autoInSede) {

			boolean confirmed = false;
			parcheggiAuto = parcheggioDao.findByAutoId(a.getId());
			for (Parcheggio p : parcheggiAuto) {
				if (p.isConfirmed())
					confirmed = true;
			}

			if (!confirmed)
				autoDaConfermare.add(a);
		}
		model.addAttribute("autoinsede", autoDaConfermare);

		return "inserimentoParcheggio";

	}

	@RequestMapping(value = "parcheggioInserito", method = RequestMethod.POST)
	public String parcheggioInserito(HttpSession session, Model model, Parcheggio parcheggio) {

		Integer idSede = (Integer) session.getAttribute("sede");
		Sede questasede = sedeDao.findById(idSede).get();
		parcheggio.setSede(questasede);
		parcheggio.setDataParch(LocalDate.now().plus(1, ChronoUnit.DAYS));
		parcheggioDao.save(parcheggio);

		return "redirect:/insparch";

	}
}
