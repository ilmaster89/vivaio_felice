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

	public Map<Object, Object> caricaSpeseSingolaAuto(Integer idAuto, LocalDate data1, LocalDate data2) {

		List<Integer> spese = spesaDao.speseAuto(idAuto, data1, data2);
		List<Date> date = spesaDao.dateManutenzione(idAuto, data1, data2);
		Map<Object, Object> reportSpese = new LinkedHashMap<Object, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if (!spese.isEmpty()) {

			for (int i = 1; i < spese.size(); i++)

				if (date.get(i).compareTo(date.get(i - 1)) == 0) {
					spese.set(i, spese.get(i) + spese.get(i - 1));
				}

			for (int i = 0; i < spese.size(); i++) {

				if (spese.get(i) == null)
					reportSpese.put(sdf.format(date.get(i)), 0);
				if (spese.get(i) != null)
					reportSpese.put(sdf.format(date.get(i)), spese.get(i));

			}

		}

		return reportSpese;
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

		LocalDateTime ldt1 = LocalDateTime.of(data1, LocalTime.MIDNIGHT);
		LocalDateTime ldt2 = LocalDateTime.of(data2, LocalTime.MIDNIGHT);
		if (data1 == null || data2 == null) {
			model.addAttribute("sedi", sedeDao.findAll());
			return "dashKm";
		}

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
			model.addAttribute("sede", sedeDao.sedeSingola(sede).toString().toLowerCase());
			model.addAttribute("data1", data1);
			model.addAttribute("data2", data2);
			return "graficoKm";
		}

		Sede sedeScelta = sedeDao.sedeSingola(sede);
		Map<Object, Object> reportAuto = new LinkedHashMap<Object, Object>();

		for (Auto a : autoDao.autoInSede(sede, LocalDate.now()))
			reportAuto.put(a.toString(), kmEffettuati(a.getId(), ldt1, ldt2));

		model.addAttribute("autoInSede", autoDao.autoInSede(sede, LocalDate.now()));
		model.addAttribute("reportAuto", reportAuto);
		session.setAttribute("dataInizio", data1);
		model.addAttribute("data1", data1);
		model.addAttribute("data2", data2);
		session.setAttribute("dataFine", data2);
		model.addAttribute("sede", sedeScelta);

		return "graficoKm";

	}

	@RequestMapping("/dettaglioAutoKm")
	public String dettaglioKm(HttpSession session, Model model, @RequestParam("auto") Integer auto) {

		LocalDate data1 = (LocalDate) session.getAttribute("dataInizio");
		LocalDate data2 = (LocalDate) session.getAttribute("dataFine");
		LocalDateTime ldt1 = LocalDateTime.of(data1, LocalTime.MIDNIGHT);
		LocalDateTime ldt2 = LocalDateTime.of(data2, LocalTime.MIDNIGHT);
		model.addAttribute("reportAuto", caricaKmSingolaAuto(auto, ldt1, ldt2));
		model.addAttribute("autoScelta", autoDao.autoDaId(auto));
		return "dettaglioKm";

	}

	@RequestMapping("/dashManu")

	public String dashboardManu(HttpSession session, Model model) {
		Integer idSede = (Integer) session.getAttribute("sede");
		model.addAttribute("autoInSede", autoDao.autoInSede(idSede, LocalDate.now()));
		return "dashManu";

	}

	@RequestMapping(value = "/graficoManu", method = RequestMethod.POST)
	public String graficoManutenzione(Model model, HttpSession session, @RequestParam("auto") Integer idAuto,
			@RequestParam("data1") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data1,
			@RequestParam("data2") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data2) {

		List<Integer> spese = spesaDao.speseAuto(idAuto, data1, data2);
		Map<Object, Object> reportSpese = caricaSpeseSingolaAuto(idAuto, data1, data2);
		Integer sommaSpese = 0;

		if (!spese.isEmpty()) {

			for (Integer x : spese) {

				if (x == null)
					x = 0;
				sommaSpese += x;
			}
		}

		model.addAttribute("reportSpese", reportSpese);
		model.addAttribute("sommaSpese", sommaSpese);

		return "graficoManu";

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

		LocalDate dataLimite = LocalDate.now().minus(settimane, ChronoUnit.WEEKS);
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
			return "graficoPrenoPassate";
		}

		Map<Object, Object> prenoPassate = caricaPrenoPassate(sede, dataLimite);
		List<String> datePreno = new ArrayList<String>();
		for (Object o : prenoPassate.keySet()) {
			String s = (String) o;
			datePreno.add(s);
		}
		model.addAttribute("reportAuto", prenoPassate);
		model.addAttribute("listaDate", datePreno);
		session.setAttribute("sedeScelta", sede);

		return "graficoPrenoPassate";

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
}
