package com.vivaio_felice.vivaio_hibernate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vivaio_felice.vivaio_hibernate.dao.AutoDao;

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
	SedeDao sedeDao;

	@RequestMapping("/insauto")
	public String insAuto(HttpSession session, Model model, Auto auto) {

		model.addAttribute("carburanti", carburanteDao.findAll());
		model.addAttribute("patenti", patenteDao.findAll());
		return "inserimentoAuto";
	}

	@RequestMapping(value = "/insertAuto", method = RequestMethod.POST)
	public String autoInserita(HttpSession session, Model model, Auto auto, Parcheggio parcheggio) {

		Sede questasede = sedeDao.sedeSingola((Integer) session.getAttribute("sede"));
		autoDao.save(auto);
		parcheggioDao.save(new Parcheggio(auto, questasede, LocalDate.now()));
		parcheggioDao.save(new Parcheggio(auto, questasede, LocalDate.now().plus(1, ChronoUnit.DAYS)));
		return "primapagina";

	}

}
