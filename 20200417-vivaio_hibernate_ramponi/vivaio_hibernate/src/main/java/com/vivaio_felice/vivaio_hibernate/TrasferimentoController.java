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

import com.vivaio_felice.vivaio_hibernate.dao.AutoJdbcDao;
import com.vivaio_felice.vivaio_hibernate.dao.ParcheggioDao;
import com.vivaio_felice.vivaio_hibernate.dao.ParcheggioJdbcDao;
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
	@Autowired
	ParcheggioJdbcDao parchJdbcDao;

	@RequestMapping("/trans")
	public String toTrans(HttpSession session, Model model, Parcheggio parcheggio) {

		Integer idSede = (Integer) session.getAttribute("sede");
		List<Auto> autoInSede = autoJdbcDao.autoInSede(idSede);
		List<Auto> autoTrasferibili = new ArrayList<Auto>();
		List<Parcheggio> parcheggiAuto = new ArrayList<Parcheggio>();
		LocalDate trasf = null;

		for (Auto a : autoInSede) {
			parcheggiAuto = parcheggioDao.findByAutoId(a.getId());

			trasf = parcheggiAuto.get(parcheggiAuto.size() - 1).dataTrasferimento(idSede);

			if (trasf == null)
				autoTrasferibili.add(a);

		}
		List<Sede> sediTrasf = sedeJdbcDao.sediEccetto(idSede);
		model.addAttribute("autoInSede", autoTrasferibili);
		model.addAttribute("sedipossibili", sediTrasf);

		return "trasferimenti";
	}

	@RequestMapping(value = "/trasferimento", method = RequestMethod.POST)
	public String autoTrasferita(HttpSession session, Model model, Parcheggio parcheggio) {

		if (parcheggio.getDataParch().compareTo(LocalDate.now().plus(1, ChronoUnit.DAYS)) == 0)
			parcheggioDao.delete(parchJdbcDao.parcheggioDomani(parcheggio.getAuto().getId()).get(0));

		parcheggioDao.save(parcheggio);
		return "primapagina";

	}

}
