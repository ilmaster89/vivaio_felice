package com.vivaio_felice.vivaio_hibernate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vivaio_felice.vivaio_hibernate.dao.AutoDao;
import com.vivaio_felice.vivaio_hibernate.dao.CausaleNotificaDao;
import com.vivaio_felice.vivaio_hibernate.dao.NotificaDao;
import com.vivaio_felice.vivaio_hibernate.dao.ParcheggioDao;
import com.vivaio_felice.vivaio_hibernate.dao.PrenotazioneDao;
import com.vivaio_felice.vivaio_hibernate.dao.SedeDao;

@Controller
public class TrasferimentoController {

	@Autowired
	ParcheggioDao parcheggioDao;
	@Autowired
	AutoDao autoDao;
	@Autowired
	SedeDao sedeDao;
	@Autowired
	PrenotazioneDao prenotazioneDao;
	@Autowired
	NotificaDao notDao;
	@Autowired
	CausaleNotificaDao cauNotDao;

	// valuto se esiste una prenotazione precedente successiva alla data di
	// trasferimento, per poi creare la notifica
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
		List<Auto> autoInSede = autoDao.autoInSede(idSede);
		List<Auto> autoTrasferibili = new ArrayList<Auto>();
		LocalDate trasf = null;

		for (Auto a : autoInSede) {

			// se un'auto è già prenotata per un trasferimento non si può farne un secondo
			// finché non arriva in sede
			trasf = parcheggioDao.ultimoParch(a.getId()).dataTrasferimento(idSede);

			if (trasf == null)
				autoTrasferibili.add(a);

		}
		// scelta della sede tra tutte tranne la corrente
		List<Sede> sediTrasf = sedeDao.sediEccetto(idSede);
		model.addAttribute("autoInSede", autoTrasferibili);
		model.addAttribute("sedipossibili", sediTrasf);

		return "trasferimenti";
	}

	@RequestMapping(value = "/trasferimento", method = RequestMethod.POST)
	public String autoTrasferita(HttpSession session, Model model, @Valid Parcheggio parcheggio, BindingResult br) {

		if (parcheggio.getDataParch().isBefore((LocalDate.now().plus(1, ChronoUnit.DAYS)))) {
			model.addAttribute("errore", "La data non può essere prima di domani, riprova.");
			return "erroreMessaggio";
		}

		// ricarico tutto se ho fatto degli errori
		if (br.hasErrors()) {
			Integer idSede = (Integer) session.getAttribute("sede");
			List<Auto> autoInSede = autoDao.autoInSede(idSede);
			List<Auto> autoTrasferibili = new ArrayList<Auto>();
			LocalDate trasf = null;

			for (Auto a : autoInSede) {

				// se un'auto è già prenotata per un trasferimento non si può farne un secondo
				// finché non arriva in sede
				trasf = parcheggioDao.ultimoParch(a.getId()).dataTrasferimento(idSede);

				if (trasf == null)
					autoTrasferibili.add(a);

			}
			// scelta della sede tra tutte tranne la corrente
			List<Sede> sediTrasf = sedeDao.sediEccetto(idSede);
			model.addAttribute("autoInSede", autoTrasferibili);
			model.addAttribute("sedipossibili", sediTrasf);
			return "trasferimenti";
		}

		// se per caso la data di trasferimento è il giorno di domani, si sostituisce il
		// record del parcheggio già inserito in automatico al login
		if (parcheggio.getDataParch().compareTo(LocalDate.now().plus(1, ChronoUnit.DAYS)) == 0)
			parcheggioDao.delete(
					parcheggioDao.parchDomani(parcheggio.getAuto().getId(), LocalDate.now().plus(1, ChronoUnit.DAYS)));

		List<Prenotazione> prenoAuto = prenotazioneDao.findByAutoId(parcheggio.getAuto().getId());

		for (Prenotazione p : prenoAuto) {

			// si generano le notifiche relative ai dipendenti che hanno prenotato per date
			// successive al trasferimento
			if (prenoSuccessiva(p.getDataInizio(), p.getDataFine(), parcheggio.getDataParch())) {
				Notifica not = new Notifica();
				not.setDipendente(p.getDipendente());
				not.setPrenotazione(p);
				not.setConferma(0);
				not.setDescrizione("Attenzione, la tua prenotazione per l'auto: " + p.getAuto().toString()
						+ ", prevista per queste date: " + p.getDataInizio() + " " + p.getDataFine()
						+ " deve essere modificata in quanto l'auto non sarà disponibile.");
				not.setCausaleNotifica(cauNotDao.causaleDaInserire(cauNotDao.notPerPrenotazione()));
				notDao.save(not);
			}

		}

		parcheggioDao.save(parcheggio);
		return "redirect:/primapagina";

	}

}
