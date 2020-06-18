package com.vivaio_felice.vivaio_hibernate;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vivaio_felice.vivaio_hibernate.dao.AutoDao;
import com.vivaio_felice.vivaio_hibernate.dao.CausaleDao;
import com.vivaio_felice.vivaio_hibernate.dao.DipendenteDao;
import com.vivaio_felice.vivaio_hibernate.dao.PrenotazioneDao;
import com.vivaio_felice.vivaio_hibernate.dao.SedeDao;
import com.vivaio_felice.vivaio_hibernate.dao.SpesaManutenzioneDao;

@Controller
public class ChartController {

	@Autowired
	PrenotazioneDao prenoDao;
	@Autowired
	AutoDao autoDao;
	@Autowired
	SpesaManutenzioneDao spesaDao;
	@Autowired
	CausaleDao causaleDao;
	@Autowired
	SedeDao sedeDao;
	@Autowired
	DipendenteDao dipDao;

	public Map<Object, Object> caricaKmSingolaAuto(Integer idAuto, LocalDateTime ldt1, LocalDateTime ldt2) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		List<Integer> kmAuto = prenoDao.kmPerGrafico(idAuto, ldt1, ldt2);
		List<java.util.Date> dateAuto = prenoDao.dataPerGrafico(idAuto, ldt1, ldt2);

		Map<Object, Object> reportAuto = new LinkedHashMap<Object, Object>();
		Integer kmPrec = prenoDao.kmPrecedenti(idAuto, ldt1);

		if (kmPrec == null)
			kmPrec = autoDao.autoDaId(idAuto).getKmIniziali();

		if (!kmAuto.isEmpty()) {
			reportAuto.put(sdf.format(dateAuto.get(0)), kmAuto.get(0) - kmPrec);

			for (int i = 1; i < dateAuto.size(); i++) {
				if (kmAuto.get(i) == null)
					reportAuto.put(sdf.format(dateAuto.get(i)), 0);
				if (kmAuto.get(i) != null) {
					reportAuto.put(sdf.format(dateAuto.get(i)), (kmAuto.get(i) - kmAuto.get(i - 1)));

				}
			}
		}

		return reportAuto;
	}

	// km effettuati da un'auto in un determinato periodo
	public Integer kmEffettuati(Integer idAuto, LocalDateTime ldt1, LocalDateTime ldt2) {

		Integer kmFinali = prenoDao.kmFinali(idAuto, ldt1, ldt2);

		Integer kmPrec = prenoDao.kmPrecedenti(idAuto, ldt1);

		if (kmFinali == null)
			return 0;

		if (kmPrec != null)
			return kmFinali - kmPrec;

		return kmFinali - autoDao.autoDaId(idAuto).getKmIniziali();

	}

	public Integer kmEffettuatiDipendente(Integer idDip, Integer idAuto, LocalDateTime ldt1, LocalDateTime ldt2) {

		List<Prenotazione> prenoDip = prenoDao.prenotazioniDipGrafico(idDip, idAuto, ldt1, ldt2);

		Integer kmPercorsiDalDip = 0;

		for (Prenotazione p : prenoDip) {
			Integer kmPrec = prenoDao.kmPrecedenti(p.getAuto().getId(),
					p.getDataInizio().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
			if (kmPrec == null)
				kmPrec = 0;

			kmPercorsiDalDip += Math.min(p.getKm() - p.getAuto().getKmIniziali(), p.getKm() - kmPrec);
		}

		return kmPercorsiDalDip;
	}

	public Map<Object, Object> caricaPrenoPassate(Integer idSede, LocalDate data) {

		Map<Object, Object> reportPrenotazioni = new LinkedHashMap<Object, Object>();

		// prendo la lista delle prenotazioni dalla data scelta
		List<Prenotazione> prenoPassate = prenoDao.prenoPassate(data);
		// preparo un array per non contare le robe due volte
		boolean[] controlliEffettuati = new boolean[prenoPassate.size()];
		// preparo una data per il controllo successivo nel ciclo
		LocalDate ld = null;

		// elimino i controlli su auto non in sede
		for (Prenotazione p0 : prenoPassate)
			if (!autoDao.autoInSede(idSede, LocalDate.now()).contains(p0.getAuto()))
				controlliEffettuati[prenoPassate.indexOf(p0)] = true;

		// ciclo nelle prenotazioni prese...
		for (Prenotazione p : prenoPassate) {

			// ...evitando quelle che sono già state controllate
			if (!controlliEffettuati[prenoPassate.indexOf(p)]) {
				Integer totPreno = 1;
				ld = p.getDataInizio().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

				// setto true per evitare un conteggio ridondante
				controlliEffettuati[prenoPassate.indexOf(p)] = true;

				// controllo tutte le prenotazioni rimanenti...
				for (Prenotazione p2 : prenoPassate) {

					// ...non ancora controllate...
					if (!controlliEffettuati[prenoPassate.indexOf(p2)])

						// per vedere se hanno la stessa data
						if (p2.getDataInizio().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
								.compareTo(ld) == 0) {

							// se la data è uguale aumento di uno il totale delle prenotazioni da caricare
							// nella mappa...
							totPreno++;

							// ...e setto true per il controllo
							controlliEffettuati[prenoPassate.indexOf(p2)] = true;
						}

				}

				reportPrenotazioni.put(p.dataFormattata(), totPreno);
			}
		}

		return reportPrenotazioni;

	}

	@RequestMapping("/dashKm")
	public String dash(Model model, HttpSession session) {

		model.addAttribute("sedi", sedeDao.findAll());
		return "dashKm";
	}

	@RequestMapping(value = "/graficoKm", method = RequestMethod.POST)
	public String graficoKm(Model model, HttpSession session,
			@RequestParam("data1") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data1,
			@RequestParam("data2") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data2,
			@RequestParam("sede") Integer sede) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		LocalDateTime ldt1 = LocalDateTime.of(data1, LocalTime.MIDNIGHT);
		LocalDateTime ldt2 = LocalDateTime.of(data2, LocalTime.MIDNIGHT);

		Sede sedeScelta = sedeDao.sedeSingola(sede);
		if (data1 == null || data2 == null) {
			model.addAttribute("sedi", sedeDao.findAll());
			return "dashKm";
		}

		model.addAttribute("titoletto", "Km percorsi");
		model.addAttribute("asseY", "Km");
		model.addAttribute("serie", "Totale Km");
		if (sede == sedeDao.tutteLeSedi()) {

			Integer kmTuttoVivaio = 0;
			Map<Object, Object> kmPerSede = new LinkedHashMap<Object, Object>();
			for (Sede s : sedeDao.sediEccetto(sedeDao.tutteLeSedi())) {

				Integer kmTotaliSede = 0;
				for (Auto a : autoDao.autoInSede(s.getId(), LocalDate.now())) {
					kmTotaliSede += kmEffettuati(a.getId(), ldt1, ldt2);
				}

				kmTuttoVivaio += kmTotaliSede;
				kmPerSede.put(s.toString(), kmTotaliSede);
			}

			kmPerSede.put(sedeDao.sedeSingola(sedeDao.tutteLeSedi()).toString(), kmTuttoVivaio);

			model.addAttribute("reportAuto", kmPerSede);
			try {
				String d1 = sdf.format(sdf1.parse(data1.toString()));
				String d2 = sdf.format(sdf1.parse(data2.toString()));
				model.addAttribute("titolo", "Km per auto nel periodo " + d1 + "-" + d2 + " per "
						+ sedeDao.sedeSingola(sede).toString().toLowerCase());

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "grafico";
		}

		Map<Object, Object> reportAuto = new LinkedHashMap<Object, Object>();

		for (Auto a : autoDao.autoInSede(sede, LocalDate.now()))
			reportAuto.put(a.toString(), kmEffettuati(a.getId(), ldt1, ldt2));

		model.addAttribute("autoInSede", autoDao.autoInSede(sede, LocalDate.now()));
		model.addAttribute("reportAuto", reportAuto);
		session.setAttribute("dataInizio", data1);
		session.setAttribute("dataFine", data2);
		try {
			String d1 = sdf.format(sdf1.parse(data1.toString()));
			String d2 = sdf.format(sdf1.parse(data2.toString()));
			model.addAttribute("titolo",
					"Km per auto nel periodo " + d1 + "-" + d2 + " per la sede di " + sedeScelta.toString());

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "grafico";

	}

	@RequestMapping("/dettaglioAutoKm")
	public String dettaglioKm(HttpSession session, Model model, @RequestParam("auto") Integer auto) {

		LocalDate data1 = (LocalDate) session.getAttribute("dataInizio");
		LocalDate data2 = (LocalDate) session.getAttribute("dataFine");
		LocalDateTime ldt1 = LocalDateTime.of(data1, LocalTime.MIDNIGHT);
		LocalDateTime ldt2 = LocalDateTime.of(data2, LocalTime.MIDNIGHT);
		model.addAttribute("reportAuto", caricaKmSingolaAuto(auto, ldt1, ldt2));
		model.addAttribute("titolo", "Dettaglio per l'auto " + autoDao.autoDaId(auto).toString());

		model.addAttribute("titoletto", "Km percorsi");
		model.addAttribute("asseY", "Km");
		model.addAttribute("serie", "Totale Km");
		return "grafico";

	}

	@RequestMapping("/dashManu")
	public String dashSpeseSede(HttpSession session, Model model) {
		model.addAttribute("ognisede", sedeDao.findAll());
		return "dashManu";
	}

	@RequestMapping(value = "/graficoManu", method = RequestMethod.POST)
	public String graficoSpeseSede(Model model, HttpSession session, @RequestParam("sede") Integer idSede,
			@RequestParam("data1") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data1,
			@RequestParam("data2") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data2) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		Map<Object, Object> reportSpeseSede = new LinkedHashMap<Object, Object>();
		Map<Object, Object> speseSedi = new LinkedHashMap<Object, Object>();
		Sede sedeScelta = sedeDao.sedeSingola(idSede);
		Integer sommaSpeseSede = 0;
		Integer sommaTotale = 0;

		model.addAttribute("titoletto", "Spese sostenute");
		model.addAttribute("serie", "Totale spese in Euro");
		model.addAttribute("asseY", "Spese");
		if (idSede == sedeDao.tutteLeSedi()) {
			for (Sede sd : sedeDao.findAll()) {
				if (sd.getId() != sedeDao.tutteLeSedi()) {
					for (Auto a : autoDao.autoInSede(sd.getId(), LocalDate.now())) {
						if (!spesaDao.speseAuto(a.getId(), data1, data2).isEmpty()) {
							for (Integer s : spesaDao.speseAuto(a.getId(), data1, data2)) {
								sommaSpeseSede += s;
								sommaTotale += s;
							}
						}
					}
				}
				if (sd.getId() == sedeDao.tutteLeSedi()) {
					speseSedi.put(sd.toString(), sommaTotale);
				} else {
					speseSedi.put(sd.toString(), sommaSpeseSede);
				}
				sommaSpeseSede = 0;

			}
			model.addAttribute("reportAuto", speseSedi);
			try {
				String d1 = sdf.format(sdf1.parse(data1.toString()));
				String d2 = sdf.format(sdf1.parse(data2.toString()));
				model.addAttribute("titolo", "Spese per auto nel periodo " + d1 + "-" + d2 + " per "
						+ sedeDao.sedeSingola(idSede).toString().toLowerCase());

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "grafico";
		}

		for (Auto a : autoDao.autoInSede(idSede, LocalDate.now())) {
			if (!spesaDao.speseAuto(a.getId(), data1, data2).isEmpty()) {
				for (Integer s : spesaDao.speseAuto(a.getId(), data1, data2)) {
					sommaSpeseSede += s;
				}
				reportSpeseSede.put(a.toString(), sommaSpeseSede);
				sommaSpeseSede = 0;
			}
		}

		try {
			String d1 = sdf.format(sdf1.parse(data1.toString()));
			String d2 = sdf.format(sdf1.parse(data2.toString()));
			model.addAttribute("titolo",
					"Spese per auto nel periodo " + d1 + "-" + d2 + " per la sede di " + sedeScelta.toString());

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("reportAuto", reportSpeseSede);
		return "grafico";
	}

	@RequestMapping("/prenotazioniFut")
	public String dashPreno(HttpSession session, Model model) {

		List<Prenotazione> prenotazioniFuture = new ArrayList<Prenotazione>();
		Integer idSede = (Integer) session.getAttribute("sede");
		for (Auto a : autoDao.autoInSede(idSede, LocalDate.now()))
			prenotazioniFuture.addAll(prenoDao.prenotazioniFuture(a.getId()));

		model.addAttribute("prenotazioni", prenotazioniFuture);
		return "dashPreno";
	}

	@RequestMapping("/dettaglioPreno")
	public String dettaglioPrenotazione(HttpSession session, Model model, @RequestParam("id") Integer id) {

		Prenotazione prenotazioneRichiesta = prenoDao.prenoDaId(id);
		SpesaManutenzione spesaRelativa = new SpesaManutenzione();

		if (prenotazioneRichiesta.getCausale().getId() != causaleDao.idLavoro())
			spesaRelativa = spesaDao.spesaDaId(id + 1);

		model.addAttribute("prenotazioneRichiesta", prenotazioneRichiesta);
		model.addAttribute("spesaRelativa", spesaRelativa);

		return "dettaglioPrenotazione";

	}

	@RequestMapping("/prenoPassate")
	public String dashPassato(HttpSession session, Model model) {
		model.addAttribute("sedi", sedeDao.findAll());
		return "dashPrenoPassate";
	}

	@RequestMapping(value = "/graficoPrenoPassate", method = RequestMethod.POST)
	public String graficoPassato(HttpSession session, Model model, @RequestParam("sede") Integer sede,
			@RequestParam("settimane") Integer settimane) {

		Sede sedeScelta = sedeDao.sedeSingola(sede);
		LocalDate dataLimite = LocalDate.now().minus(settimane, ChronoUnit.WEEKS);

		model.addAttribute("titoletto", "Prenotazioni effettuate");
		model.addAttribute("asseY", "Prenotazioni");
		model.addAttribute("serie", "Totale prenotazioni");
		if (sede == sedeDao.tutteLeSedi()) {

			Integer prenoGlobali = 0;
			Map<Object, Object> reportSede = new LinkedHashMap<Object, Object>();
			for (Sede s : sedeDao.sediEccetto(sedeDao.tutteLeSedi())) {

				Integer totPrenoSede = 0;
				Map<Object, Object> prenoPassateSede = caricaPrenoPassate(s.getId(), dataLimite);
				for (Object o : prenoPassateSede.values()) {
					Integer x = (Integer) o;
					totPrenoSede += x;
					prenoGlobali += x;
				}

				reportSede.put(s.toString(), totPrenoSede);
			}

			reportSede.put(sedeDao.sedeSingola(sedeDao.tutteLeSedi()).toString(), prenoGlobali);
			model.addAttribute("reportAuto", reportSede);
			model.addAttribute("titolo", "Prenotazioni effettuate nelle ultime " + settimane + " settimane per "
					+ sedeDao.sedeSingola(sedeDao.tutteLeSedi()).toString().toLowerCase());
			return "grafico";
		}

		Map<Object, Object> prenoPassate = caricaPrenoPassate(sede, dataLimite);
		List<String> datePreno = new ArrayList<String>();
		for (Object o : prenoPassate.keySet()) {
			String s = (String) o;
			datePreno.add(s);
		}
		model.addAttribute("reportAuto", prenoPassate);
		model.addAttribute("listaDate", datePreno);
		model.addAttribute("titolo", "Prenotazioni effettuate nelle ultime " + settimane + " settimane nella sede di "
				+ sedeScelta.toString());
		session.setAttribute("sedeScelta", sede);

		return "grafico";
	}

	@RequestMapping(value = "/dettaglioPrenoPassate", method = RequestMethod.POST)
	public String dettaglioPrenoPassate(HttpSession session, Model model,
			@RequestParam("dataScelta") String dataScelta) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<Prenotazione> prenotazioniDelGiornoScelto = new ArrayList<Prenotazione>();
		Integer idSede = (Integer) session.getAttribute("sedeScelta");
		try {

			java.util.Date dataDaStringa = sdf.parse(dataScelta);

			LocalDateTime ldt1 = LocalDateTime
					.of(dataDaStringa.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MIDNIGHT);
			LocalDateTime ldt2 = ldt1.plus(23, ChronoUnit.HOURS);
			ldt2 = ldt2.plus(59, ChronoUnit.MINUTES);
			ldt2 = ldt2.plus(59, ChronoUnit.SECONDS);

			for (Auto a : autoDao.autoInSede(idSede, LocalDate.now())) {

				prenotazioniDelGiornoScelto.addAll(prenoDao.prenoDiUnGiorno(a.getId(), ldt1, ldt2));

			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		model.addAttribute("prenotazioni", prenotazioniDelGiornoScelto);
		return "dashPreno";
	}

	@RequestMapping("/kmDip")
	public String kmDip(HttpSession session, Model model) {
		Integer idSede = (Integer) session.getAttribute("sede");
		model.addAttribute("dipendenti", dipDao.dipendentiInSede(idSede));
		return "dashDipe";
	}

	@RequestMapping(value = "/graficoDipe", method = RequestMethod.POST)
	public String graficoDipe(Model model, HttpSession session,
			@RequestParam("data1") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data1,
			@RequestParam("data2") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data2,
			@RequestParam("dipendente") Integer idDip) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		LocalDateTime ldt1 = LocalDateTime.of(data1, LocalTime.MIDNIGHT);
		LocalDateTime ldt2 = LocalDateTime.of(data2, LocalTime.MIDNIGHT);

		Dipendente dipScelto = dipDao.dipDaId(idDip);
		Integer idSede = (Integer) session.getAttribute("sede");
		if (data1 == null || data2 == null) {
			model.addAttribute("sedi", sedeDao.findAll());
			return "dashDipe";
		}

		model.addAttribute("titoletto", "Km percorsi");
		model.addAttribute("asseY", "Km");
		model.addAttribute("serie", "Totale Km");
		try {
			String d1 = sdf.format(sdf1.parse(data1.toString()));
			String d2 = sdf.format(sdf1.parse(data2.toString()));
			model.addAttribute("titolo",
					"Km per auto nel periodo " + d1 + "-" + d2 + " percorsi da " + dipScelto.toString());

		} catch (ParseException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<Object, Object> reportAuto = new LinkedHashMap<Object, Object>();

		for (Auto a : autoDao.autoInSede(idSede, LocalDate.now()))
			reportAuto.put(a.toString(), kmEffettuatiDipendente(idDip, a.getId(), ldt1, ldt2));

		model.addAttribute("reportAuto", reportAuto);

		return "grafico";

	}
}
