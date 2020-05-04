package com.vivaio_felice.vivaio_hibernate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vivaio_felice.vivaio_hibernate.dao.AutoJdbcDao;
import com.vivaio_felice.vivaio_hibernate.dao.CausaleDao;
import com.vivaio_felice.vivaio_hibernate.dao.ParcheggioDao;
import com.vivaio_felice.vivaio_hibernate.dao.PrenotazioneDao;
import com.vivaio_felice.vivaio_hibernate.dao.PrenotazioneJdbcDao;

@Controller
public class PrenotazioneController {

	public static boolean datesMatch(Date d1, Date d2, Date d3, Date d4) {

		if (d1.toInstant().compareTo(d3.toInstant()) == 0) {
			if (d1.compareTo(d3) == 0)
				return true;
			if (d2.compareTo(d4) == 0)
				return true;
			if (d1.after(d3) && d2.before(d4))
				return true;
			if (d1.before(d3) && d2.after(d4))
				return true;
			if ((d1.after(d3) && d1.before(d4)) && d2.after(d4))
				return true;
			if (d1.before(d3) && (d2.after(d3) && d2.before(d4)))
				return true;

		}

		return false;
	}

	public static boolean datesMatchWithTrans(Date d1, Date d2, Date d3, Date d4, LocalDate trans) {

		ZoneId dzi = ZoneId.systemDefault();
		Instant inInizio = d1.toInstant();
		LocalDate ldInizio = inInizio.atZone(dzi).toLocalDate();
		Instant inFine = d2.toInstant();
		LocalDate ldFine = inFine.atZone(dzi).toLocalDate();

		if (ldInizio.compareTo(trans) >= 0 || ldFine.compareTo(trans) >= 0)
			return true;

		else {

			if (d1.toInstant().compareTo(d3.toInstant()) == 0) {
				if (d1.compareTo(d3) == 0)
					return true;
				if (d2.compareTo(d4) == 0)
					return true;
				if (d1.after(d3) && d2.before(d4))
					return true;
				if (d1.before(d3) && d2.after(d4))
					return true;
				if ((d1.after(d3) && d1.before(d4)) && d2.after(d4))
					return true;
				if (d1.before(d3) && (d2.after(d3) && d2.before(d4)))
					return true;

			}
		}

		return false;

	}

	public static Date d1 = null;
	public static Date d2 = null;
	public static List<Prenotazione> prenotazioniNulle = null;
	public static Prenotazione ultima = null;

	@Autowired
	AutoJdbcDao autoJdbcDao;
	@Autowired
	PrenotazioneDao prenotazioneDao;
	@Autowired
	CausaleDao causaleDao;
	@Autowired
	ParcheggioDao parcheggioDao;
	@Autowired
	PrenotazioneJdbcDao prenoJdbcDao;

	@RequestMapping("/prenota")
	public String prenotazione(HttpSession session, Model model, Prenotazione prenotazione) {

		// lavorare sul costruttore
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//		Date data;
//		try {
//			data = sdf.parse("05/05/2020 09:00");
//			Date data2 = sdf.parse("05/05/2020 10:00");
//			Prenotazione p = new Prenotazione(1, 1, 1, 1, data, data2, 50000);
//			System.out.println(p.toString());
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		d1 = null;
		d2 = null;
		return "prenota";
	}

	@RequestMapping("/torna")
	public String ritorno(HttpSession session, Model model, Prenotazione prenotazione) {
		return "prenota";
	}

	@RequestMapping(value = "/richiestadiprenotazione", method = RequestMethod.POST)
	public String ottieniAuto(HttpSession session, Model model, Prenotazione prenotazione,
			@RequestParam("dataInizio") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") Date dataInizio,
			@RequestParam("dataFine") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") Date dataFine) {

		if (dataFine.compareTo(dataInizio) <= 0)
			return "erroreData";

		d1 = dataInizio;
		d2 = dataFine;

		Integer idSede = (Integer) session.getAttribute("sede");
		List<Auto> autoNellaSede = autoJdbcDao.autoInSede(idSede);
		List<Prenotazione> prenotazioniSingolaAuto = new ArrayList<Prenotazione>();
		List<Auto> autoPrenotabili = new ArrayList<Auto>();
		List<Parcheggio> parcheggiAuto = new ArrayList<Parcheggio>();
		LocalDate trans = null;

		for (Auto a : autoNellaSede) {
			prenotazioniSingolaAuto = prenotazioneDao.findByAutoId(a.getId());
			parcheggiAuto = parcheggioDao.findByAutoId(a.getId());

			boolean transfer = false;

			trans = parcheggiAuto.get(parcheggiAuto.size() - 1).dataTrasferimento(idSede);
			if (trans != null)
				transfer = true;

			if (transfer) {
				if (prenotazioniSingolaAuto.isEmpty())
					autoPrenotabili.add(a);

				else {
					boolean matchTrans = false;

					for (Prenotazione preno : prenotazioniSingolaAuto) {

						if (datesMatchWithTrans(dataInizio, dataFine, preno.getDataInizio(), preno.getDataFine(),
								trans))
							matchTrans = true;

					}

					if (!matchTrans)
						autoPrenotabili.add(a);
				}
			}

			if (!transfer) {
				if (prenotazioniSingolaAuto.isEmpty())
					autoPrenotabili.add(a);

				else {
					boolean match = false;

					for (Prenotazione preno : prenotazioniSingolaAuto) {

						if (datesMatch(dataInizio, dataFine, preno.getDataInizio(), preno.getDataFine()))
							match = true;

					}

					if (!match)
						autoPrenotabili.add(a);
				}
			}
		}

		model.addAttribute("autoDisponibili", autoPrenotabili);
		return "prenopossibili";

	}

	@RequestMapping(value = "/confermadiprenotazione", method = RequestMethod.POST)
	public String autoPrenotata(HttpSession session, Model model, Prenotazione prenotazione) {

		prenotazione.setDipendente((Dipendente) session.getAttribute("loggedUser"));
		prenotazione.setCausale(causaleDao.findById(3).get());
		prenotazione.setDataInizio(d1);
		prenotazione.setDataFine(d2);
		prenotazioneDao.save(prenotazione);
		d1 = null;
		d2 = null;
		return "primapagina";
	}

	@RequestMapping("/km")
	public String insKm(HttpSession session, Model model, Prenotazione prenotazione) {

		prenotazioniNulle = new ArrayList<Prenotazione>();
		Dipendente d = (Dipendente) session.getAttribute("loggedUser");
		Integer idDip = d.getId();
		List<Prenotazione> prenotazioniDip = prenotazioneDao.findByDipendenteId(idDip);

		for (Prenotazione p : prenotazioniDip) {
			if (p.getKm() == null)
				prenotazioniNulle.add(p);
		}

		if (!prenotazioniNulle.isEmpty())
			ultima = prenotazioniNulle.get(prenotazioniNulle.size() - 1);

		model.addAttribute("ultima", ultima);

		return "inserimentoKm";
	}

	@RequestMapping(value = "/kminseriti", method = RequestMethod.POST)
	public String inseriti(HttpSession session, Model model, Prenotazione prenotazione) {

		prenotazione.setId(ultima.getId());
		prenotazione.setDipendente(ultima.getDipendente());
		prenotazione.setAuto(ultima.getAuto());
		prenotazione.setCausale(ultima.getCausale());
		prenotazione.setDataInizio(ultima.getDataInizio());
		prenotazione.setDataFine(ultima.getDataFine());

		prenotazioneDao.save(prenotazione);
		prenotazioniNulle = new ArrayList<Prenotazione>();
		ultima = null;
		return "primapagina";

	}
}