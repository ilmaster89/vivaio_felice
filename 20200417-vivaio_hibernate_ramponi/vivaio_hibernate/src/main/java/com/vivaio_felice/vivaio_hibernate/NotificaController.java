package com.vivaio_felice.vivaio_hibernate;

import java.time.LocalDate;
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

	public List<Auto> autoPossibili(List<Auto> autoNellaSede, boolean patC, boolean neoP, Integer idSede,
			Date dataInizio, Date dataFine) {

		LocalDate trans = null;
		List<Auto> autoPrenotabili = new ArrayList<Auto>();
		for (Auto a : autoNellaSede) {

			List<Prenotazione> prenotazioniSingolaAuto = prenotazioneDao.prenoAuto(a.getId());
			boolean transfer = false;
			boolean match = false;

			trans = parcheggioDao.ultimoParch(a.getId()).dataTrasferimento(idSede);
			if (trans != null)
				transfer = true;

			if (!prenotazioniSingolaAuto.isEmpty()) {
				for (Prenotazione p : prenotazioniSingolaAuto)
					if ((transfer && PrenotazioneController.datesMatchWithTrans(dataInizio, dataFine, p.getDataInizio(),
							p.getDataFine(), trans))
							|| (!transfer && PrenotazioneController.datesMatch(dataInizio, dataFine, p.getDataInizio(),
									p.getDataFine())))
						match = true;

			}

			if ((!patC && a.getPatente().getId() == patenteDao.idB()) || patC)
				if (a.okForNeoP() || (!a.okForNeoP() && !neoP))
					if (!match)
						autoPrenotabili.add(a);

		}

		return autoPrenotabili;

	}

//	in attesa delle modifiche di Marco, questo metodo annulla le
//	notifiche così che non vengano più visualizzate
	@RequestMapping("/notificaok")
	public String notOk(HttpSession session, Model model, Notifica notifica, @RequestParam("id") Integer id, Auto auto,
			Prenotazione prenotazione) {

		Notifica notDaCambiare = notificaDao.notDaId(id);
		if (notDaCambiare.getAuto() != null) {
			session.setAttribute("autoDaRinnovare", notDaCambiare.getAuto());
			return "rinnovoAssicurazione";
		}

		if (notDaCambiare.getPrenotazione() != null && notDaCambiare.getDescrizione().contains("km")) {
			Prenotazione nuovap = prenotazioneDao.findById(notDaCambiare.getPrenotazione().getId()).get();

			Prenotazione precedente = null;
			Prenotazione ultima = nuovap;

			if (ultima != null)
				precedente = prenotazioneDao.precedente(ultima.getAuto().getId());

			session.setAttribute("nuovap", nuovap);
			session.setAttribute("ultima", ultima);
			session.setAttribute("precedente", precedente);
			return "inserimentoKm";
		}
		
		if (notDaCambiare.getPrenotazione() != null) {
			Prenotazione nuovaPren = prenotazioneDao.findById(notDaCambiare.getPrenotazione().getId()).get();

			session.setAttribute("nuova", nuovaPren);

			Date dataInizio = notDaCambiare.getPrenotazione().getDataInizio();
			Date dataFine = notDaCambiare.getPrenotazione().getDataFine();

			Integer idSede = (Integer) session.getAttribute("sede");
			Dipendente d = (Dipendente) session.getAttribute("loggedUser");
			boolean patC = d.patenteC(posPatDao.findByDipendenteId(d.getId()), patenteDao.idC());
			boolean neoP = d.neoP(posPatDao.findByDipendenteId(d.getId()), patenteDao.idC());
			List<Auto> autoNellaSede = autoDao.autoInSede(idSede, LocalDate.now());
			List<Auto> autoPrenotabili = autoPossibili(autoNellaSede, patC, neoP, idSede, dataInizio, dataFine);
			model.addAttribute("autoDisponibili", autoPrenotabili);
			session.setAttribute("dataInizio", dataInizio);
			session.setAttribute("dataFine", dataFine);

			return "prenopossibili";
		}

		notDaCambiare.setConferma(1);
		notificaDao.save(notDaCambiare);
		return "redirect:/primapagina";
	}

}
