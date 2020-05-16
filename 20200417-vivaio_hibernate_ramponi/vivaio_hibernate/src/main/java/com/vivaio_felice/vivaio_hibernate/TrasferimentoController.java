package com.vivaio_felice.vivaio_hibernate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vivaio_felice.vivaio_hibernate.dao.AutoJdbcDao;
import com.vivaio_felice.vivaio_hibernate.dao.NotificaDao;
import com.vivaio_felice.vivaio_hibernate.dao.ParcheggioDao;
import com.vivaio_felice.vivaio_hibernate.dao.PrenotazioneDao;
import com.vivaio_felice.vivaio_hibernate.dao.SedeDao;

@Controller
public class TrasferimentoController {

	@Autowired
	ParcheggioDao parcheggioDao;
	@Autowired
	AutoJdbcDao autoJdbcDao;
	@Autowired
	SedeDao sedeDao;
	@Autowired
	PrenotazioneDao prenotazioneDao;
	@Autowired
	NotificaDao notDao;

	public static boolean prenoSuccessiva(Date d1, Date d2, LocalDate trans) {

		ZoneId dzi = ZoneId.systemDefault();
		Instant d1inst = d1.toInstant();
		Instant d2inst = d2.toInstant();
		LocalDate ld1 = d1inst.atZone(dzi).toLocalDate();
		LocalDate ld2 = d2inst.atZone(dzi).toLocalDate();

		if (ld1.compareTo(trans) >= 0 || ld2.compareTo(trans) >= 0)
			return true;

		return false;

	}

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
		List<Sede> sediTrasf = sedeDao.sediEccetto(idSede);
		model.addAttribute("autoInSede", autoTrasferibili);
		model.addAttribute("sedipossibili", sediTrasf);

		return "trasferimenti";
	}

	@RequestMapping(value = "/trasferimento", method = RequestMethod.POST)
	public String autoTrasferita(HttpSession session, Model model, Parcheggio parcheggio) {

		if (parcheggio.getDataParch().compareTo(LocalDate.now().plus(1, ChronoUnit.DAYS)) == 0)
			parcheggioDao.delete(
					parcheggioDao.parchDomani(parcheggio.getAuto().getId(), LocalDate.now().plus(1, ChronoUnit.DAYS)));

		List<Prenotazione> prenoAuto = prenotazioneDao.findByAutoId(parcheggio.getAuto().getId());

		for (Prenotazione p : prenoAuto) {

			if (prenoSuccessiva(p.getDataInizio(), p.getDataFine(), parcheggio.getDataParch())) {
				Notifica not = new Notifica();
				not.setDipendente(p.getDipendente());
				not.setConferma(0);
				not.setDescrizione("Attenzione, la tua prenotazione per l'auto: " + p.getAuto().toString()
						+ ", prevista per queste date: " + p.getDataInizio() + " " + p.getDataFine()
						+ " deve essere modificata in quanto l'auto non sarà disponibile.");
				notDao.save(not);
			}

		}

		parcheggioDao.save(parcheggio);
		return "primapagina";

	}

}
