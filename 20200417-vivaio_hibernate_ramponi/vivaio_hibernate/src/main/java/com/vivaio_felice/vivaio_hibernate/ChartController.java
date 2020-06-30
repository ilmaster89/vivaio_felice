package com.vivaio_felice.vivaio_hibernate;

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
import com.vivaio_felice.vivaio_hibernate.dao.SedeDipendenteDao;
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
	@Autowired
	SedeDipendenteDao sedeDipendenteDao;

	// faccio la mappa che uso per il grafico del dettaglio di una singola auto
	public Map<Object, Object> caricaKmSingolaAuto(Integer idAuto, LocalDateTime ldt1, LocalDateTime ldt2) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		// prendo km e date nel periodo considerato
		List<Integer> kmAuto = prenoDao.kmPerGrafico(idAuto, ldt1, ldt2);
		List<java.util.Date> dateAuto = prenoDao.dataPerGrafico(idAuto, ldt1, ldt2);

		// preparo la mappa vuota
		Map<Object, Object> reportAuto = new LinkedHashMap<Object, Object>();
		// carico eventuali km precedenti al periodo...
		Integer kmPrec = prenoDao.kmPrecedenti(idAuto, ldt1);

		// ... e uso quelli iniziali dell'auto se non ci sono, perché non ci sarebbero
		// mai state nel caso prenotazioni
		if (kmPrec == null)
			kmPrec = autoDao.autoDaId(idAuto).getKmIniziali();

		if (!kmAuto.isEmpty()) {

			// carico manualmente il primo record della mappa, con la prima data e la
			// differenza tra i km dello stesso indice e i kmPrec
			reportAuto.put(sdf.format(dateAuto.get(0)), kmAuto.get(0) - kmPrec);

			for (int i = 1; i < dateAuto.size(); i++) {

				// ciclo per tutti gli altri valori, ad ogni km si toglie il precedente per
				// mostrare effettivamente i km fatti e non il totale
				reportAuto.put(sdf.format(dateAuto.get(i)), (kmAuto.get(i) - kmAuto.get(i - 1)));

			}
		}

		return reportAuto;
	}

	// km effettuati da un'auto in un determinato periodo
	public Integer kmEffettuati(Integer idAuto, LocalDateTime ldt1, LocalDateTime ldt2) {

		// carico i km più vicini alla data finale del periodo considerato
		Integer kmFinali = prenoDao.kmFinali(idAuto, ldt1, ldt2);

		// carico gli ultimi km fatti prima del periodo
		Integer kmPrec = prenoDao.kmPrecedenti(idAuto, ldt1);

		// in questo caso non ci sarebbero prenotazioni nel periodo considerato e quindi
		// ZERO km fatti
		if (kmFinali == null)
			return 0;

		// sottraggo i km precedenti solo se esistono...
		if (kmPrec != null)
			return kmFinali - kmPrec;

		// ... altrimenti quelli iniziali dell'auto
		return kmFinali - autoDao.autoDaId(idAuto).getKmIniziali();

	}

	// km che ha fatto un SINGOLO DIPENDENTE in un periodo con una singola auto
	public Integer kmEffettuatiDipendente(Integer idDip, Integer idAuto, LocalDateTime ldt1, LocalDateTime ldt2) {

		// carico tutte le prenotazioni del dipendente in questione e di un'auto
		// (ciclando nel request tra tutte le auto in sede)
		List<Prenotazione> prenoDip = prenoDao.prenotazioniDipGrafico(idDip, idAuto, ldt1, ldt2);

		// preparo l'integer che poi aumenterò con i controlli successivi prima di
		// passarlo al metodo
		Integer kmPercorsiDalDip = 0;

		for (Prenotazione p : prenoDip) {

			// cerco i km precedenti ALLA SINGOLA PRENOTAZIONE E NON AL PERIODO, quindi
			// vanno bene anche km fatti nello stesso periodo, passando alla query l'auto
			// della prenotazione su cui sto ciclando e la sua data, opportunamente
			// trasformata in ldt
			Integer kmPrec = prenoDao.kmPrecedenti(p.getAuto().getId(),
					p.getDataInizio().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

			// se non ci sono km precedenti (il controllo si estende in automatico ad oltre
			// il periodo considerato perché non ho messo un limite inferiore) uso i km
			// iniziali dell'auto
			if (kmPrec == null) {
				kmPrec = p.getAuto().getKmIniziali();

			}

			// aumento il totale dei km percorsi della differenza tra i km segnati nella
			// prenotazione e i km precedenti (qualsiasi essi siano)
			kmPercorsiDalDip += (p.getKm() - kmPrec);
		}

		return kmPercorsiDalDip;
	}

	// riempio la mappa per mostrare le prenotazioni passate nella sede scelta
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

		// questi attributi li carico per usarli indifferentemente in tutti i grafici
		model.addAttribute("titoletto", "Km percorsi");
		model.addAttribute("asseY", "Km");
		model.addAttribute("serie", "Totale Km");

		// se si sceglie "tutte le sedi"...
		if (sede == sedeDao.tutteLeSedi()) {

			// integer che rappresenta tutti i km di tutte le sedi sommate
			Integer kmTuttoVivaio = 0;
			Map<Object, Object> kmPerSede = new LinkedHashMap<Object, Object>();

			for (Sede s : sedeDao.sediEccetto(sedeDao.tutteLeSedi())) {
				// ciclo all'interno delle sedi...
				Integer kmTotaliSede = 0;

				// ... e aggiungo ai km della singola sede tutti quelli effettuati dalle sue
				// auto
				for (Auto a : autoDao.autoInSede(s.getId(), LocalDate.now())) {
					kmTotaliSede += kmEffettuati(a.getId(), ldt1, ldt2);
				}

				// al termine di ogni ciclo di sede aumento anche i km di tutto il vivaio, che a
				// differenza di quelli della sede non tornano a zero ad ogni ciclo!
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

		// qui invece calcolo i km effettuati in una sede qualora se ne scegliesse una
		// specifica...
		Map<Object, Object> reportAuto = new LinkedHashMap<Object, Object>();

		for (Auto a : autoDao.autoInSede(sede, LocalDate.now()))
			// ... usando il metodo corrispondente
			reportAuto.put(a.toString(), kmEffettuati(a.getId(), ldt1, ldt2));

		// carico anche la lista delle auto in sede per eventuali dettagli
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

	// all'interno di una sede scelgo una singola auto di cui controllare i km
	@RequestMapping("/dettaglioAutoKm")
	public String dettaglioKm(HttpSession session, Model model, @RequestParam("auto") Integer auto) {

		LocalDate data1 = (LocalDate) session.getAttribute("dataInizio");
		LocalDate data2 = (LocalDate) session.getAttribute("dataFine");
		LocalDateTime ldt1 = LocalDateTime.of(data1, LocalTime.MIDNIGHT);
		LocalDateTime ldt2 = LocalDateTime.of(data2, LocalTime.MIDNIGHT);

		// vedi metodo in alto
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

	// mostra le spese sostenute in sede per la manutenzione
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

		// se si sceglie "tutte le sedi" si aumentano due integer come per i km (vedi
		// sopra)
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

		// se si sceglie una singola sede...
		for (Auto a : autoDao.autoInSede(idSede, LocalDate.now())) {
			if (!spesaDao.speseAuto(a.getId(), data1, data2).isEmpty()) {
				for (Integer s : spesaDao.speseAuto(a.getId(), data1, data2)) {
					// ... si aumenta solo uno dei due integer iniziali
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

			e.printStackTrace();
		}
		model.addAttribute("reportAuto", reportSpeseSede);
		return "grafico";
	}

	// mostra le prenotazioni che ci saranno in futuro, con possibilità di dettaglio
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

		// l'id che uso per trovare la prenotazione lo prendo direttamente dal form
		Prenotazione prenotazioneRichiesta = prenoDao.prenoDaId(id);
		// la spesa relativa a quella manutenzione è per ora vuota...
		SpesaManutenzione spesaRelativa = new SpesaManutenzione();

		// ... e la riempio solo se la prenotazione non è per lavoro ma appunto per
		// manutenzione
		if (prenotazioneRichiesta.getCausale().getId() != causaleDao.idLavoro())
			// uso l'id aumentato di 1 perché la spesa viene salvata immediatamente dopo la
			// prenotazione
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
		// la data limite non è indicata nel form ma si trova sulla base delle settimane
		// richieste
		LocalDate dataLimite = LocalDate.now().minus(settimane, ChronoUnit.WEEKS);

		model.addAttribute("titoletto", "Prenotazioni effettuate");
		model.addAttribute("asseY", "Prenotazioni");
		model.addAttribute("serie", "Totale prenotazioni");

		// similmente agli altri metodi, se si scelgono tutte le sedi si usano i due
		// integer per trovare i report
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

		// per il dettaglio delle date prendo le date che ho e le carico in una lista di
		// stringhe, in modo da usarle poi in un select
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

	// uso lo stesso template per le prenotazioni future applicandolo a quelle che
	// sono già passate, in modo da sfruttare anche il dettaglio di ogni singola
	// prenotazione
	@RequestMapping(value = "/dettaglioPrenoPassate", method = RequestMethod.POST)
	public String dettaglioPrenoPassate(HttpSession session, Model model,
			@RequestParam("dataScelta") String dataScelta) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<Prenotazione> prenotazioniDelGiornoScelto = new ArrayList<Prenotazione>();
		Integer idSede = (Integer) session.getAttribute("sedeScelta");
		try {

			java.util.Date dataDaStringa = sdf.parse(dataScelta);

			// genero le due date limite per ottenere tutte le prenotazioni di un singolo
			// giorno, impostando manualmente gli orari, mezzanotte e le 23:59:59
			LocalDateTime ldt1 = LocalDateTime
					.of(dataDaStringa.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MIDNIGHT);
			LocalDateTime ldt2 = ldt1.plus(23, ChronoUnit.HOURS);
			ldt2 = ldt2.plus(59, ChronoUnit.MINUTES);
			ldt2 = ldt2.plus(59, ChronoUnit.SECONDS);

			for (Auto a : autoDao.autoInSede(idSede, LocalDate.now())) {

				prenotazioniDelGiornoScelto.addAll(prenoDao.prenoDiUnPeriodo(a.getId(), ldt1, ldt2));

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

		// nel caso del grafico dei km fatti da un dipendente utilizzo solamente la mia
		// sede, perché è una valutazione più "intima"
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

		// per tutte le auto in sede carico il totale dei km usando il metodo dichiarato
		// sopra
		for (Auto a : autoDao.autoInSede(idSede, LocalDate.now()))
			reportAuto.put(a.toString(), kmEffettuatiDipendente(idDip, a.getId(), ldt1, ldt2));

		model.addAttribute("reportAuto", reportAuto);

		return "grafico";

	}

	@RequestMapping("/distribuzione")
	public String dettaglioGenerale(HttpSession httpSession, Model model, Dipendente dipendente, Auto auto) {

		List<Sede> allSedi = (List<Sede>) sedeDao.findAll();

		// Mappa per AUTO in Sede
		Map<Object, Object> graficoAuto = new LinkedHashMap<Object, Object>();

		for (int i = 0; i < allSedi.size(); i++) {
			Integer sedeScelta = allSedi.get(i).getId();
			// Se la Sede è uguale a 13 si carica la somma di tutte le Auto
			if (sedeScelta == sedeDao.tutteLeSedi()) {
				Integer numAuto = autoDao.quantitaAuto();
				graficoAuto.put(allSedi.get(i).citta, numAuto);
			}
			// Altrimenti si passa ogni sede sommando le auto per ogni singola Sede
			else {
				List<Auto> autoInSede = autoDao.autoInSede(sedeScelta, LocalDate.now());
				Integer numAutoSede = 0;
				// Se ci sono Auto in Sede si aumenta la variabile
				for (int x = 0; x < autoInSede.size(); x++) {
					numAutoSede++;
				}
				// Carico i dati nelle mappe, se è vuota una sede aggiungo "0"
				if (autoInSede.isEmpty()) {
					graficoAuto.put(allSedi.get(i).citta, 0);
				} else
					graficoAuto.put(allSedi.get(i).citta, numAutoSede);
			}
			model.addAttribute("graficoAuto", graficoAuto);
		}

		// Creo mappa per DIPENDENTI in sede
		Map<Object, Object> graficoDipendenti = new LinkedHashMap<Object, Object>();
		for (int i = 0; i < allSedi.size(); i++) {

			Integer sedeSpecifica = allSedi.get(i).getId();
			// Se la Sede è uguale a 13 si carica la somma di tutti i dipendenti
			if (sedeSpecifica == sedeDao.tutteLeSedi()) {
				Integer allDip = dipDao.quantitaDip();
				graficoDipendenti.put(allSedi.get(i).citta, allDip);
			}
			// Altrimenti si passa ogni sede sommando i dipendenti per ogni singola Sede
			else {
				List<Integer> dipInSede = sedeDipendenteDao.dipendentiInSede(sedeSpecifica);
				Integer numDipSede = 0;
				// Se ci sono Dipendenti in Sede si aumenta la variabile
				for (int x = 0; x < dipInSede.size(); x++) {
					numDipSede++;
				}
				// Carico i dati nelle mappe, se è vuota una sede aggiungo "0"
				if (dipInSede.isEmpty()) {
					graficoDipendenti.put(allSedi.get(i).citta, 0);
				} else
					graficoDipendenti.put(allSedi.get(i).citta, numDipSede);
			}
		}

		model.addAttribute("titolo", "Distribuzione di Auto e Dipendenti tra le sedi");

		model.addAttribute("graficoDipendenti", graficoDipendenti);
		return "graficoDensita";
	}
}