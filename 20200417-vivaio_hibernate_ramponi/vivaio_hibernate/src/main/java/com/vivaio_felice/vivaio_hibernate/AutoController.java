package com.vivaio_felice.vivaio_hibernate;

import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

//	Ogni lunedì alle 9 viene controllata la data di assicurazione
//	di tutte le auto nel parco, in modo da generare eventuali 
//	notifiche se la scadenza è entro il mese
	@Scheduled(cron = "0 0 9 * * MON")
	public void assicurazioniInScadenza() {

		LocalDate traUnMese = LocalDate.now().plus(1, ChronoUnit.MONTHS);
		for (Auto a : autoDao.findAll()) {

			LocalDate dataAss = a.getDataAss().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate scadenza = dataAss.plus(1, ChronoUnit.YEARS);
			System.out.println(scadenza);
			if (scadenza.compareTo(traUnMese) <= 0) {

				String avviso = "L'assicurazione dell'auto: " + a.toString() + " scadrà il " + scadenza
						+ ", ricordati di rinnovarla!";
				System.out.println("notifica fatta");
				notificaDao.save(new Notifica(avviso, a, 0));
			}

		}

	}

	// metodo schedulato per caricare le auto nel parcheggio piuttosto che farle
	// caricare al login del dipendente
	@Scheduled(cron = "0 48 10 * * ?")
	public void confermaParcheggi() {

		for (Auto a : autoDao.autoParcheggiate(LocalDate.now())) {

			Sede sede = sedeDao.sedeSingola(parcheggioDao.sedeOdierna(a.getId(), LocalDate.now()));
			Parcheggio domani = parcheggioDao.parchDomani(a.getId(), LocalDate.now().plus(1, ChronoUnit.DAYS));

			if (domani == null) {
				parcheggioDao.save(new Parcheggio(a, sede, LocalDate.now().plus(1, ChronoUnit.DAYS)));
			}
		}

	}

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
