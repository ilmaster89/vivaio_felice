package com.vivaio_felice.vivaio_hibernate;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vivaio_felice.vivaio_hibernate.dao.AutoJdbcDao;
import com.vivaio_felice.vivaio_hibernate.dao.ParcheggioDao;
import com.vivaio_felice.vivaio_hibernate.dao.SedeDao;
import com.vivaio_felice.vivaio_hibernate.dao.SedeJdbcDao;

@Controller
public class TrasferimentoController {

	@Autowired
	ParcheggioDao parcheggioDao;
	@Autowired
	AutoJdbcDao autoJdbcDao;
	@Autowired
	SedeDao sedeDao;
	@Autowired
	SedeJdbcDao sedeJdbcDao;

	@RequestMapping("/trans")
	public String toTrans(HttpSession session, Model model, Parcheggio parcheggio) {

		Integer idSede = (Integer) session.getAttribute("sede");
		List<Auto> autoInSede = autoJdbcDao.autoInSede(idSede);
		List<Sede> sediTrasf = sedeJdbcDao.sediEccetto(idSede);
		model.addAttribute("autoInSede", autoInSede);
		model.addAttribute("sedipossibili", sediTrasf);

		return "trasferimenti";
	}

	@RequestMapping(value = "/trasferimento", method = RequestMethod.POST)
	public String autoTrasferita(HttpSession session, Model model, Parcheggio parcheggio) {

		parcheggioDao.save(parcheggio);
		return "primapagina";

	}

}
