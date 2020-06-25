package com.vivaio_felice.vivaio_hibernate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vivaio_felice.vivaio_hibernate.dao.AutoDao;
import com.vivaio_felice.vivaio_hibernate.dao.CausaleNotificaDao;
import com.vivaio_felice.vivaio_hibernate.dao.NotificaDao;
import com.vivaio_felice.vivaio_hibernate.dao.ParcheggioDao;
import com.vivaio_felice.vivaio_hibernate.dao.PatenteDao;
import com.vivaio_felice.vivaio_hibernate.dao.PossessoPatentiDao;
import com.vivaio_felice.vivaio_hibernate.dao.PrenotazioneDao;

@Controller
public class NotificaController {

	@Autowired
	NotificaDao notificaDao;
	@Autowired
	AutoDao autoDao;
	@Autowired
	PrenotazioneDao prenotazioneDao;
	@Autowired
	PossessoPatentiDao posPatDao;
	@Autowired
	PatenteDao patenteDao;
	@Autowired
	ParcheggioDao parcheggioDao;
	@Autowired
	CausaleNotificaDao cauNotDao;
	@Autowired
	PatenteDao patDao;

	public List<Auto> autoPrenotabili(List<Auto> autoNellaSede, boolean patC, boolean neoP, Integer idSede,
			LocalDateTime dataInizio, LocalDateTime dataFine) {

		List<Auto> autoPrenotabili = new ArrayList<Auto>();

		for (Auto a : autoNellaSede)
			if (prenotazioneDao.prenoContrastanti(a.getId(), dataInizio, dataFine).isEmpty())
				if (parcheggioDao.trasferimentoContrastante(a.getId(), dataFine, idSede).isEmpty())
					if ((!patC && a.getPatente().getId() == patDao.idB()) || patC)
						if (a.okForNeoP() || (!a.okForNeoP() && !neoP))
							autoPrenotabili.add(a);

		return autoPrenotabili;
	}

	// confermare le notifiche in modo che non vengano più mostrate
	@RequestMapping("/notificaok")
	public String notOk(HttpSession session, Model model, Notifica notifica, @RequestParam("id") Integer id, Auto auto,
			Prenotazione prenotazione) {

		// identifico quale sarà la notifica su cui andare a lavorare
		Notifica notDaCambiare = notificaDao.notDaId(id);

		// se ha un auto di riferimento allora si va a rinnovare la data di
		// assicurazione, dato che la causale sarà quella
		if (notDaCambiare.getCausaleNotifica().getId() == cauNotDao.notPerAssicurazione()) {
			session.setAttribute("autoDaRinnovare", notDaCambiare.getAuto());
			return "rinnovoAssicurazione";
		}

		// se ha una prenotazione di riferimento e ha la parola km si rimanda
		// all'inserimento km...
		if (notDaCambiare.getCausaleNotifica().getId() == cauNotDao.notPerKilometri()) {
			Prenotazione nuovap = prenotazioneDao.findById(notDaCambiare.getPrenotazione().getId()).get();

			Prenotazione precedente = null;
			Prenotazione ultima = nuovap;

			if (ultima != null)
				precedente = prenotazioneDao.precedente(ultima.getAuto().getId());

			// ...caricando questi attributi in modo da valutare se si tratta di un
			// inserimento volontario oppure si è arrivati dalla notifica
			session.setAttribute("nuovap", nuovap);
			session.setAttribute("ultima", ultima);
			session.setAttribute("precedente", precedente);
			return "inserimentoKm";
		}

		// se non ci sono km da inserire allora è una prenotazione da cambiare
		if (notDaCambiare.getCausaleNotifica().getId() == cauNotDao.notPerPrenotazione()) {
			Prenotazione nuovaPren = prenotazioneDao.findById(notDaCambiare.getPrenotazione().getId()).get();

			// necessario per valutare se nuova prenotazione oppure una che viene cambiata
			session.setAttribute("nuova", nuovaPren);

			// si utilizza lo stesso metodo per valutare quali auto possano essere prenotate
			// nelle date già presenti. Se non ci dovessero essere auto allora si può
			// cancellare la prenotazione ma non modificare, in quanto creerebbe troppi
			// problemi agli altri
			Date dataInizio = notDaCambiare.getPrenotazione().getDataInizio();
			Date dataFine = notDaCambiare.getPrenotazione().getDataFine();

			LocalDateTime ldtInizio = LocalDateTime.ofInstant(dataInizio.toInstant(), ZoneId.systemDefault());
			LocalDateTime ldtFine = LocalDateTime.ofInstant(dataFine.toInstant(), ZoneId.systemDefault());

			Integer idSede = (Integer) session.getAttribute("sede");
			Dipendente d = (Dipendente) session.getAttribute("loggedUser");
			boolean patC = d.patenteC(posPatDao.findByDipendenteId(d.getId()), patenteDao.idC());
			boolean neoP = d.neoP(posPatDao.findByDipendenteId(d.getId()), patenteDao.idB());
			List<Auto> autoNellaSede = autoDao.autoInSede(idSede, LocalDate.now());
			List<Auto> autoPrenotabili = autoPrenotabili(autoNellaSede, patC, neoP, idSede, ldtInizio, ldtFine);
			model.addAttribute("autoDisponibili", autoPrenotabili);
			session.setAttribute("dataInizio", dataInizio);
			session.setAttribute("dataFine", dataFine);

			return "prenopossibili";
		}

		// questo rimane qui qualora non ci fossero azioni particolari da compiere ma
		// solo notifiche da leggere e accettare
		notDaCambiare.setConferma(1);
		notificaDao.save(notDaCambiare);
		return "redirect:/primapagina";
	}

}
