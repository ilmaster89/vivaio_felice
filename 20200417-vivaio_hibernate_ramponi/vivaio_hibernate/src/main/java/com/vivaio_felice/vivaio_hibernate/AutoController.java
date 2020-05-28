package com.vivaio_felice.vivaio_hibernate;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vivaio_felice.vivaio_hibernate.dao.AutoDao;
import com.vivaio_felice.vivaio_hibernate.dao.CarburanteDao;
import com.vivaio_felice.vivaio_hibernate.dao.NotificaDao;
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
	SedeDao sedeDao;
	@Autowired
	NotificaDao notificaDao;

	@RequestMapping("/insauto")
	public String insAuto(HttpSession session, Model model, Auto auto) {

		model.addAttribute("carburanti", carburanteDao.findAll());
		model.addAttribute("patenti", patenteDao.findAll());
		return "inserimentoAuto";
	}

	@RequestMapping(value = "/insertAuto", method = RequestMethod.POST)
	public String autoInserita(HttpSession session, Model model, @Valid Auto auto, BindingResult bindingResult,
			Parcheggio parcheggio) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("carburanti", carburanteDao.findAll());
			model.addAttribute("patenti", patenteDao.findAll());
			return "inserimentoAuto";
		}
		Sede questasede = sedeDao.sedeSingola((Integer) session.getAttribute("sede"));
		autoDao.save(auto);
		parcheggioDao.save(new Parcheggio(auto, questasede, LocalDate.now()));
		parcheggioDao.save(new Parcheggio(auto, questasede, LocalDate.now().plus(1, ChronoUnit.DAYS)));
		return "primapagina";

	}

	@RequestMapping(value = "/assRinnovata", method = RequestMethod.POST)
	public String rinnovo(HttpSession session, Model model, @Valid Auto auto, BindingResult br) {

		if (br.hasErrors())
			return "rinnovoAssicurazione";
		Date nuovaAss = auto.getDataAss();
		auto = (Auto) session.getAttribute("autoDaRinnovare");
		auto.setDataAss(nuovaAss);
		autoDao.save(auto);
		Notifica confermata = notificaDao.notificheAuto(auto.getId());
		confermata.setConferma(1);
		notificaDao.save(confermata);
		session.removeAttribute("autoDaRinnovare");
		return "redirect:/primapagina";

	}

}
